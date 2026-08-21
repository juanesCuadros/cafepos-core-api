package com.cafepos.core.shared.seguridad;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final UsuarioRefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final TenantRolConfigRepository tenantRolConfigRepository;
    private final EventoSeguridadService eventoSeguridadService;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final TokenPairIssuer tokenPairIssuer;

    public RefreshTokenService(UsuarioRefreshTokenRepository refreshTokenRepository,
                                UsuarioRepository usuarioRepository,
                                TenantRolConfigRepository tenantRolConfigRepository,
                                EventoSeguridadService eventoSeguridadService,
                                RefreshTokenIssuer refreshTokenIssuer,
                                TokenPairIssuer tokenPairIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.tenantRolConfigRepository = tenantRolConfigRepository;
        this.eventoSeguridadService = eventoSeguridadService;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.tokenPairIssuer = tokenPairIssuer;
    }

    /**
     * Rotacion: el token recibido queda revocado apenas se usa, sin importar
     * si el par nuevo se llega a usar. Un token ya revocado que se vuelve a
     * presentar es señal de robo — se registra en evento_seguridad antes de
     * rechazar (ver EventoSeguridad.TIPO_REFRESH_TOKEN_REUSO).
     *
     * Ademas de vigencia (expira_en), se chequea minutos_inactividad de
     * tenant_rol_config para el rol del usuario: si paso mas tiempo del
     * permitido desde el ultimo refresh, se rechaza igual y se fuerza login
     * nuevo, aunque el token todavia no haya vencido por TTL.
     */
    @Transactional
    public TokenPair ejecutar(String rawRefreshToken) {
        String hash = refreshTokenIssuer.hash(rawRefreshToken);
        UsuarioRefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(RefreshTokenInvalidoException::new);

        if (token.isRevocado()) {
            eventoSeguridadService.registrar(token.getTenantId(), token.getUsuarioId(),
                    EventoSeguridad.TIPO_REFRESH_TOKEN_REUSO, "Refresh token ya revocado presentado de nuevo");
            throw new RefreshTokenInvalidoException();
        }
        if (token.estaVencido()) {
            throw new RefreshTokenInvalidoException();
        }

        Usuario usuario = usuarioRepository.findById(token.getUsuarioId())
                .orElseThrow(RefreshTokenInvalidoException::new);
        TenantRolConfig rolConfig = tenantRolConfigRepository
                .findByTenantIdAndRolId(usuario.getTenantId(), usuario.getRolId())
                .orElseThrow(RefreshTokenInvalidoException::new);
        if (token.minutosDeInactividad() > rolConfig.getMinutosInactividad()) {
            // No hace falta persistir el revocar() aca: la excepcion de abajo
            // hace rollback de esta transaccion igual, y el token sigue
            // rechazado en cualquier intento futuro porque ultimo_uso_en no
            // avanza (la comparacion contra minutos_inactividad sigue dando
            // el mismo resultado).
            throw new RefreshTokenInvalidoException();
        }

        token.revocar();
        refreshTokenRepository.save(token);
        return tokenPairIssuer.emitir(usuario);
    }
}

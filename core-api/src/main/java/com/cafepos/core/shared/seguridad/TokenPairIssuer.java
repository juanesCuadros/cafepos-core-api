package com.cafepos.core.shared.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Compartido entre login, refresh y cambio de password inicial: los tres
 * terminan emitiendo un par access+refresh nuevo de la misma forma.
 */
@Component
public class TokenPairIssuer {

    private final UsuarioRefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final RolRepository rolRepository;
    private final long refreshTokenTtlHours;

    public TokenPairIssuer(UsuarioRefreshTokenRepository refreshTokenRepository,
                            JwtService jwtService,
                            RefreshTokenIssuer refreshTokenIssuer,
                            RolRepository rolRepository,
                            @Value("${cafepos.refresh-token.ttl-hours}") long refreshTokenTtlHours) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.rolRepository = rolRepository;
        this.refreshTokenTtlHours = refreshTokenTtlHours;
    }

    public TokenPair emitir(Usuario usuario) {
        RefreshTokenIssuer.Emitido emitido = refreshTokenIssuer.generar();
        OffsetDateTime expiraEn = OffsetDateTime.now().plusHours(refreshTokenTtlHours);
        refreshTokenRepository.save(new UsuarioRefreshToken(
                usuario.getTenantId(), usuario.getId(), emitido.hash(), expiraEn));
        String accessToken = jwtService.issueAccessToken(usuario);
        String rolNombre = rolRepository.getReferenceById(usuario.getRolId()).getNombre();
        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getId(), usuario.getNombre(), usuario.getCorreo(), rolNombre, usuario.getTenantId());
        return new TokenPair(accessToken, emitido.rawToken(), usuarioResponse, usuario.isDebeCambiarPassword());
    }
}

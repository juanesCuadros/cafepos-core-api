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
    private final long refreshTokenTtlDays;

    public TokenPairIssuer(UsuarioRefreshTokenRepository refreshTokenRepository,
                            JwtService jwtService,
                            RefreshTokenIssuer refreshTokenIssuer,
                            @Value("${cafepos.refresh-token.ttl-days}") long refreshTokenTtlDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public TokenPair emitir(Usuario usuario) {
        RefreshTokenIssuer.Emitido emitido = refreshTokenIssuer.generar();
        OffsetDateTime expiraEn = OffsetDateTime.now().plusDays(refreshTokenTtlDays);
        refreshTokenRepository.save(new UsuarioRefreshToken(
                usuario.getTenantId(), usuario.getId(), emitido.hash(), expiraEn));
        String accessToken = jwtService.issueAccessToken(usuario);
        return new TokenPair(accessToken, emitido.rawToken(), jwtService.accessTokenTtlSeconds(),
                usuario.isDebeCambiarPassword());
    }
}

package com.cafepos.admin.auth.application;

import com.cafepos.admin.auth.domain.SuperadminRefreshToken;
import com.cafepos.admin.auth.domain.SuperadminRefreshTokenRepository;
import com.cafepos.admin.auth.infrastructure.security.JwtService;
import com.cafepos.admin.auth.infrastructure.security.RefreshTokenIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Compartido entre login y refresh: ambos terminan emitiendo un par
 * access+refresh nuevo de la misma forma (login sobre una sesion nueva,
 * refresh sobre la rotacion de una existente).
 */
@Component
public class TokenPairIssuer {

    private final SuperadminRefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final long refreshTokenTtlDays;

    public TokenPairIssuer(SuperadminRefreshTokenRepository refreshTokenRepository,
                            JwtService jwtService,
                            RefreshTokenIssuer refreshTokenIssuer,
                            @Value("${cafepos.admin.refresh-token.ttl-days}") long refreshTokenTtlDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public TokenPair emitir(Integer superadminId) {
        RefreshTokenIssuer.Emitido emitido = refreshTokenIssuer.generar();
        OffsetDateTime expiraEn = OffsetDateTime.now().plusDays(refreshTokenTtlDays);
        refreshTokenRepository.save(new SuperadminRefreshToken(superadminId, emitido.hash(), expiraEn));
        String accessToken = jwtService.issueAccessToken(superadminId);
        return new TokenPair(accessToken, emitido.rawToken(), jwtService.accessTokenTtlSeconds());
    }
}

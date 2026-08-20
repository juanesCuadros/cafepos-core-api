package com.cafepos.admin.auth.application;

import com.cafepos.admin.auth.domain.RefreshTokenInvalidoException;
import com.cafepos.admin.auth.domain.SuperadminRefreshToken;
import com.cafepos.admin.auth.domain.SuperadminRefreshTokenRepository;
import com.cafepos.admin.auth.infrastructure.security.RefreshTokenIssuer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final SuperadminRefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenIssuer refreshTokenIssuer;
    private final TokenPairIssuer tokenPairIssuer;

    public RefreshTokenService(SuperadminRefreshTokenRepository refreshTokenRepository,
                                RefreshTokenIssuer refreshTokenIssuer,
                                TokenPairIssuer tokenPairIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.tokenPairIssuer = tokenPairIssuer;
    }

    /** Rotacion: el token recibido queda revocado apenas se usa, sin importar si el nuevo par se llega a usar. */
    @Transactional
    public TokenPair ejecutar(String rawRefreshToken) {
        String hash = refreshTokenIssuer.hash(rawRefreshToken);
        SuperadminRefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(RefreshTokenInvalidoException::new);
        if (token.isRevocado() || token.estaVencido()) {
            throw new RefreshTokenInvalidoException();
        }
        token.revocar();
        refreshTokenRepository.save(token);
        return tokenPairIssuer.emitir(token.getSuperadminId());
    }
}

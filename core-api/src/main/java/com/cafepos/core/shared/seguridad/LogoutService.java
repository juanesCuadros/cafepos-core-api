package com.cafepos.core.shared.seguridad;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Idempotente a proposito (ver AuthController): token inexistente o ya
 * revocado responden 200 igual — no hace falta revelar el estado interno
 * de un token que de cualquier forma ya no sirve para nada.
 */
@Service
public class LogoutService {

    private final UsuarioRefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenIssuer refreshTokenIssuer;

    public LogoutService(UsuarioRefreshTokenRepository refreshTokenRepository,
                          RefreshTokenIssuer refreshTokenIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenIssuer = refreshTokenIssuer;
    }

    @Transactional
    public void ejecutar(String rawRefreshToken) {
        String hash = refreshTokenIssuer.hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            if (!token.isRevocado()) {
                token.revocar();
                refreshTokenRepository.save(token);
            }
        });
    }
}

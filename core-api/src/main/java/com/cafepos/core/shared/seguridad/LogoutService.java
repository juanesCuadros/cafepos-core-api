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
    private final JwtBlacklistService jwtBlacklistService;
    private final JwtService jwtService;

    public LogoutService(UsuarioRefreshTokenRepository refreshTokenRepository,
                          RefreshTokenIssuer refreshTokenIssuer,
                          JwtBlacklistService jwtBlacklistService,
                          JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.jwtBlacklistService = jwtBlacklistService;
        this.jwtService = jwtService;
    }

    @Transactional
    public void ejecutar(String rawRefreshToken, String accessToken) {
        if (accessToken != null) {
            try {
                var claims = jwtService.parseClaims(accessToken);
                jwtBlacklistService.blacklistToken(accessToken, claims.getExpiration());
            } catch (Exception e) {
                // If it's already expired or invalid, it's fine.
            }
        }
        String hash = refreshTokenIssuer.hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            if (!token.isRevocado()) {
                token.revocar();
                refreshTokenRepository.save(token);
            }
        });
    }
}

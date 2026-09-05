package com.cafepos.admin.auth;

import com.cafepos.admin.auth.application.RefreshTokenService;
import com.cafepos.admin.auth.application.TokenPair;
import com.cafepos.admin.auth.application.TokenPairIssuer;
import com.cafepos.admin.auth.domain.RefreshTokenInvalidoException;
import com.cafepos.admin.auth.domain.SuperadminRefreshToken;
import com.cafepos.admin.auth.domain.SuperadminRefreshTokenRepository;
import com.cafepos.admin.auth.infrastructure.security.RefreshTokenIssuer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private SuperadminRefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenIssuer refreshTokenIssuer;

    @Mock
    private TokenPairIssuer tokenPairIssuer;

    @InjectMocks
    private RefreshTokenService service;

    @Test
    void ejecutar_tokenValido_revocaYEmitenNuevoPar() {
        String rawToken = "raw-refresh-token-xyz";
        String hash = "hash-sha256-xyz";
        SuperadminRefreshToken token = new SuperadminRefreshToken(1, hash, OffsetDateTime.now().plusDays(7));

        when(refreshTokenIssuer.hash(rawToken)).thenReturn(hash);
        when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(token));
        when(tokenPairIssuer.emitir(1)).thenReturn(new TokenPair("newAccessToken", "newRefreshToken", 600));

        TokenPair nuevoPar = service.ejecutar(rawToken);

        assertNotNull(nuevoPar);
        assertEquals("newAccessToken", nuevoPar.accessToken());
        assertEquals("newRefreshToken", nuevoPar.refreshToken());
        assertTrue(token.isRevocado());
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void ejecutar_tokenYaRevocado_lanzaRefreshTokenInvalidoException() {
        String rawToken = "raw-refresh-token-revocado";
        String hash = "hash-revocado";
        SuperadminRefreshToken token = new SuperadminRefreshToken(1, hash, OffsetDateTime.now().plusDays(7));
        token.revocar();

        when(refreshTokenIssuer.hash(rawToken)).thenReturn(hash);
        when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(token));

        assertThrows(RefreshTokenInvalidoException.class, () -> service.ejecutar(rawToken));
    }

    @Test
    void ejecutar_tokenVencido_lanzaRefreshTokenInvalidoException() {
        String rawToken = "raw-refresh-token-vencido";
        String hash = "hash-vencido";
        SuperadminRefreshToken token = new SuperadminRefreshToken(1, hash, OffsetDateTime.now().minusDays(1));

        when(refreshTokenIssuer.hash(rawToken)).thenReturn(hash);
        when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(token));

        assertThrows(RefreshTokenInvalidoException.class, () -> service.ejecutar(rawToken));
    }
}

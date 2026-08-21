package com.cafepos.core.shared.seguridad;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Genera el token opaco que se entrega al cliente y su hash SHA-256, que es
 * lo unico que se persiste (ver UsuarioRefreshToken). SHA-256 y no bcrypt:
 * el token ya nace con entropia alta (32 bytes de SecureRandom), asi que
 * buscar por igualdad de hash es seguro y permite un WHERE token_hash = ?
 * directo — mismo criterio que admin-api.
 */
@Component
public class RefreshTokenIssuer {

    private static final SecureRandom RANDOM = new SecureRandom();

    public record Emitido(String rawToken, String hash) {
    }

    public Emitido generar() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return new Emitido(rawToken, hash(rawToken));
    }

    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

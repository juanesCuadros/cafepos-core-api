package com.cafepos.admin.auth.infrastructure.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Emite access tokens de admin-api. Llave, issuer y audience propios,
 * separados de core-api a proposito (ver CLAUDE.md) — nunca compartir el
 * mismo signing-key entre los dos servicios.
 */
@Component
public class JwtService {

    private final SecretKey signingKey;
    private final String issuer;
    private final String audience;
    private final Duration accessTokenTtl;

    public JwtService(@Value("${cafepos.admin.jwt.signing-key}") String signingKey,
                       @Value("${cafepos.admin.jwt.issuer}") String issuer,
                       @Value("${cafepos.admin.jwt.audience}") String audience,
                       @Value("${cafepos.admin.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    }

    public String issueAccessToken(Integer superadminId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(superadminId.toString())
                .claim("superadmin_id", superadminId)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtl)))
                .signWith(signingKey)
                .compact();
    }

    public long accessTokenTtlSeconds() {
        return accessTokenTtl.toSeconds();
    }

    /**
     * Valida firma, issuer y audience, y devuelve el superadmin_id (via
     * "sub", no del claim custom — evita depender de como Jackson deserializa
     * el tipo numerico del claim). Lanza JwtException si el token no es
     * valido por cualquier motivo (firma, expiracion, issuer/audience).
     */
    public Integer parseSuperadminId(String token) {
        String subject = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        try {
            return Integer.valueOf(subject);
        } catch (NumberFormatException e) {
            throw new JwtException("Claim 'sub' invalido: " + subject, e);
        }
    }
}

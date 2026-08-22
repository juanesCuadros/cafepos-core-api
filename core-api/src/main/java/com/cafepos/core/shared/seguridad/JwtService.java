package com.cafepos.core.shared.seguridad;

import io.jsonwebtoken.Claims;
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
 * Emite y valida access tokens de core-api. Llave, issuer y audience
 * propios, separados de admin-api a proposito — nunca compartir el mismo
 * signing-key entre los dos servicios.
 */
@Component
public class JwtService {

    private static final String CLAIM_TENANT_ID = "tenant_id";
    private static final String CLAIM_ROL_ID = "rol_id";
    private static final String CLAIM_NOMBRE = "nombre";
    private static final String CLAIM_CORREO = "correo";
    private static final String CLAIM_ROL = "rol";
    private static final String CLAIM_DEBE_CAMBIAR_PASSWORD = "debe_cambiar_password";

    private final SecretKey signingKey;
    private final String issuer;
    private final String audience;
    private final Duration accessTokenTtl;

    public JwtService(@Value("${cafepos.jwt.signing-key}") String signingKey,
                       @Value("${cafepos.jwt.issuer}") String issuer,
                       @Value("${cafepos.jwt.audience}") String audience,
                       @Value("${cafepos.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    }

    public String issueAccessToken(Usuario usuario) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim(CLAIM_TENANT_ID, usuario.getTenantId())
                .claim(CLAIM_ROL_ID, usuario.getRolId())
                .claim(CLAIM_NOMBRE, usuario.getNombre())
                .claim(CLAIM_CORREO, usuario.getCorreo())
                .claim(CLAIM_ROL, usuario.getRol().getNombre())
                .claim(CLAIM_DEBE_CAMBIAR_PASSWORD, usuario.isDebeCambiarPassword())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtl)))
                .signWith(signingKey)
                .compact();
    }

    /** Lanza JwtException si el token no es valido por cualquier motivo (firma, expiracion, issuer/audience). */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Integer usuarioId(Claims claims) {
        try {
            return Integer.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new JwtException("Claim 'sub' invalido: " + claims.getSubject(), e);
        }
    }

    public Integer tenantId(Claims claims) {
        return claims.get(CLAIM_TENANT_ID, Integer.class);
    }

    public Integer rolId(Claims claims) {
        return claims.get(CLAIM_ROL_ID, Integer.class);
    }

    public boolean debeCambiarPassword(Claims claims) {
        return Boolean.TRUE.equals(claims.get(CLAIM_DEBE_CAMBIAR_PASSWORD, Boolean.class));
    }

    public String nombre(Claims claims) {
        return claims.get(CLAIM_NOMBRE, String.class);
    }

    public String correo(Claims claims) {
        return claims.get(CLAIM_CORREO, String.class);
    }

    public String rol(Claims claims) {
        return claims.get(CLAIM_ROL, String.class);
    }
}

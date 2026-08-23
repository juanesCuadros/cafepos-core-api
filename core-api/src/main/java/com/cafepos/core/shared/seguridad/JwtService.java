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
 * Emite y valida access tokens de core-api, y tambien los pin_token de PIN
 * de step-up (ver PinVerificarService/PinStepUpService) — misma llave,
 * issuer y audience que un access token normal a proposito (mismo
 * signing-key de core-api, nunca compartido con admin-api), diferenciados
 * SOLO por el claim "typ" (ver TYPE_PIN_STEPUP) para que ningun validador
 * pueda confundir uno con otro.
 */
@Component
public class JwtService {

    private static final String CLAIM_TENANT_ID = "tenant_id";
    private static final String CLAIM_ROL_ID = "rol_id";
    private static final String CLAIM_NOMBRE = "nombre";
    private static final String CLAIM_CORREO = "correo";
    private static final String CLAIM_ROL = "rol";
    private static final String CLAIM_DEBE_CAMBIAR_PASSWORD = "debe_cambiar_password";

    private static final String CLAIM_TYP = "typ";
    private static final String TYPE_PIN_STEPUP = "pin_stepup";
    private static final String CLAIM_PERMISO_ID = "permiso_id";
    private static final String CLAIM_RECURSO_TIPO = "recurso_tipo";
    private static final String CLAIM_RECURSO_ID = "recurso_id";

    private final SecretKey signingKey;
    private final String issuer;
    private final String audience;
    private final Duration accessTokenTtl;
    private final Duration pinStepUpTokenTtl;

    public JwtService(@Value("${cafepos.jwt.signing-key}") String signingKey,
                       @Value("${cafepos.jwt.issuer}") String issuer,
                       @Value("${cafepos.jwt.audience}") String audience,
                       @Value("${cafepos.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes,
                       @Value("${cafepos.pin-authorization.token-ttl-minutes}") long pinStepUpTokenTtlMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
        this.pinStepUpTokenTtl = Duration.ofMinutes(pinStepUpTokenTtlMinutes);
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

    /**
     * pin_token de PIN de step-up (ver PinVerificarService). NOTA DE DISEÑO:
     * este token no tiene enforcement de un solo uso real (no hay tabla de
     * tokens consumidos) — la proteccion es el TTL corto (2 minutos por
     * defecto, ver cafepos.pin-authorization.token-ttl-minutes) mas estar
     * atado al recurso especifico (permisoId + recursoTipo + recursoId,
     * verificados EXACTOS por PinStepUpService.validar). Si mas adelante se
     * necesita single-use estricto, hace falta una tabla de tokens ya
     * usados — fuera de alcance por ahora.
     */
    public String issuePinStepUpToken(Integer tenantId, Integer usuarioAutorizaId, Integer permisoId,
                                       String recursoTipo, Integer recursoId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(usuarioAutorizaId.toString())
                .claim(CLAIM_TYP, TYPE_PIN_STEPUP)
                .claim(CLAIM_TENANT_ID, tenantId)
                .claim(CLAIM_PERMISO_ID, permisoId)
                .claim(CLAIM_RECURSO_TIPO, recursoTipo)
                .claim(CLAIM_RECURSO_ID, recursoId)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(pinStepUpTokenTtl)))
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

    /** true solo si el claim "typ" es exactamente TYPE_PIN_STEPUP — nunca confundir con un access token normal. */
    public boolean esPinStepUp(Claims claims) {
        return TYPE_PIN_STEPUP.equals(claims.get(CLAIM_TYP, String.class));
    }

    public Integer permisoId(Claims claims) {
        return claims.get(CLAIM_PERMISO_ID, Integer.class);
    }

    public String recursoTipo(Claims claims) {
        return claims.get(CLAIM_RECURSO_TIPO, String.class);
    }

    public Integer recursoId(Claims claims) {
        return claims.get(CLAIM_RECURSO_ID, Integer.class);
    }
}

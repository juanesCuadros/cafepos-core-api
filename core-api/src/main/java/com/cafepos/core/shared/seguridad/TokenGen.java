import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.time.Instant;
import java.time.Duration;

public class TokenGen {
    public static void main(String[] args) {
        String key = "dev-only-signing-key-nunca-usar-en-produccion-cambiar-por-variable-de-entorno";
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject("1")
                .claim("tenant_id", 3)
                .claim("rol_id", 1)
                .claim("nombre", "Jefe Demo")
                .claim("correo", "jefe@cafeteriademo.com")
                .claim("rol", "administrador")
                .claim("debe_cambiar_password", false)
                .issuer("cafepos-core-api")
                .audience().add("cafepos-core-api").and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(24))))
                .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)))
                .compact();
        System.out.println(token);
    }
}

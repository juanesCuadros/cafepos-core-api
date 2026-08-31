package com.cafepos.core;

import org.junit.jupiter.api.Test;
import java.time.Instant;

public class TokenGeneratorTest {

    @Test
    public void generateToken() {
        String key = "dev-only-signing-key-nunca-usar-en-produccion-cambiar-por-variable-de-entorno";
        Instant now = Instant.now();
        String token = io.jsonwebtoken.Jwts.builder()
                .subject("1")
                .claim("tenant_id", 6)
                .claim("rol_id", 1)
                .claim("nombre", "Jefe Demo")
                .claim("correo", "jefe@cafeteriademo.com")
                .claim("rol", "administrador")
                .claim("debe_cambiar_password", false)
                .issuer("cafepos-core-api")
                .audience().add("cafepos-core-api").and()
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(now.plus(java.time.Duration.ofHours(24))))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(key.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
        
        System.out.println("====== GENERATED TOKEN ======");
        System.out.println(token);
        System.out.println("=============================");
    }
}

package com.cafepos.core.shared.seguridad;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {

    // K: hash del token, V: timestamp de expiración (ms)
    private final Cache<String, Long> blacklistCache;

    public JwtBlacklistService() {
        this.blacklistCache = Caffeine.newBuilder()
                .expireAfter(new Expiry<String, Long>() {
                    @Override
                    public long expireAfterCreate(String key, Long expirationTimeMillis, long currentTime) {
                        long remaining = expirationTimeMillis - System.currentTimeMillis();
                        return remaining > 0 ? TimeUnit.MILLISECONDS.toNanos(remaining) : 0;
                    }

                    @Override
                    public long expireAfterUpdate(String key, Long expirationTimeMillis, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(String key, Long expirationTimeMillis, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .build();
    }

    public void blacklistToken(String token, Date expiration) {
        if (token == null || expiration == null) {
            return;
        }
        long expTime = expiration.getTime();
        if (expTime > System.currentTimeMillis()) {
            blacklistCache.put(hash(token), expTime);
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null) {
            return false;
        }
        return blacklistCache.getIfPresent(hash(token)) != null;
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

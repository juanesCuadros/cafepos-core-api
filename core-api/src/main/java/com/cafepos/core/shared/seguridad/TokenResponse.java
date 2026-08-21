package com.cafepos.core.shared.seguridad;

public record TokenResponse(String accessToken, String refreshToken, long expiresIn, boolean debeCambiarPassword) {

    public static TokenResponse de(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), pair.expiresInSeconds(),
                pair.debeCambiarPassword());
    }
}

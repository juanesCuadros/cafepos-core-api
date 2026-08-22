package com.cafepos.core.shared.seguridad;

public record TokenResponse(String accessToken, String refreshToken, UsuarioResponse usuario, boolean debeCambiarPassword) {

    public static TokenResponse de(TokenPair pair) {
        return new TokenResponse(pair.accessToken(), pair.refreshToken(), pair.usuario(), pair.debeCambiarPassword());
    }
}

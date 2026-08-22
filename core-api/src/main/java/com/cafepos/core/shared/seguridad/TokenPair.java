package com.cafepos.core.shared.seguridad;

public record TokenPair(String accessToken, String refreshToken, UsuarioResponse usuario, boolean debeCambiarPassword) {
}

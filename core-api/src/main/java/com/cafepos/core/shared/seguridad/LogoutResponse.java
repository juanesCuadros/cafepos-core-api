package com.cafepos.core.shared.seguridad;

public record LogoutResponse(String mensaje) {

    public static final LogoutResponse SESION_CERRADA = new LogoutResponse("Sesión cerrada");
}

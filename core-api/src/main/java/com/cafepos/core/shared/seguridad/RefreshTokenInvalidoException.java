package com.cafepos.core.shared.seguridad;

/** Token inexistente, ya revocado (posible reuso), vencido, o sesion inactiva por mas del limite del rol. */
public class RefreshTokenInvalidoException extends RuntimeException {

    public RefreshTokenInvalidoException() {
        super("Sesión inválida o expirada, inicia sesión de nuevo");
    }
}

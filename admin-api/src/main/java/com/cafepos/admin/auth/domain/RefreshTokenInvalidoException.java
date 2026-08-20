package com.cafepos.admin.auth.domain;

/** Token inexistente, ya revocado (posible reuso) o vencido. */
public class RefreshTokenInvalidoException extends RuntimeException {

    public RefreshTokenInvalidoException() {
        super("Sesión inválida o expirada, inicia sesión de nuevo");
    }
}

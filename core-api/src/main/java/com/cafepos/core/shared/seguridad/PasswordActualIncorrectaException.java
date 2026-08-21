package com.cafepos.core.shared.seguridad;

/** La passwordActual enviada a /auth/cambiar-password-inicial no coincide con el hash guardado. */
public class PasswordActualIncorrectaException extends RuntimeException {

    public PasswordActualIncorrectaException() {
        super("La contraseña actual no es correcta");
    }
}

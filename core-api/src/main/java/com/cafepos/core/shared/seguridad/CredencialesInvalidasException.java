package com.cafepos.core.shared.seguridad;

/**
 * Mensaje generico a proposito: correo inexistente, password incorrecto y
 * cuenta inactiva usan esta misma excepcion. Nunca revelar cual de los tres
 * fue — ver LoginService.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Correo o contraseña incorrectos");
    }
}

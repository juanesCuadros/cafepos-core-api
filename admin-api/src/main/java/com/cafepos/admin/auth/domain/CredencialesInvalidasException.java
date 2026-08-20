package com.cafepos.admin.auth.domain;

/**
 * Mensaje generico a proposito: correo inexistente, password incorrecto y
 * cuenta inactiva usan esta misma excepcion. Nunca revelar cual de los tres
 * fue — ver auth.application.LoginSuperadminService.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Correo o contraseña incorrectos");
    }
}

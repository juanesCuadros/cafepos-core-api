package com.cafepos.core.shared.seguridad;

/** Cuenta bloqueada tras 5 intentos fallidos de login (RN-008) — ver Usuario.estaBloqueado(). */
public class CuentaBloqueadaException extends RuntimeException {

    public static final String CODIGO = "CUENTA_BLOQUEADA";

    public CuentaBloqueadaException() {
        super("Cuenta bloqueada por intentos fallidos. Intenta de nuevo en 15 minutos.");
    }
}

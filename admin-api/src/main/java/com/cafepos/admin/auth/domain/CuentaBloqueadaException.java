package com.cafepos.admin.auth.domain;

public class CuentaBloqueadaException extends RuntimeException {
    public CuentaBloqueadaException() {
        super("Tu cuenta ha sido bloqueada temporalmente por múltiples intentos fallidos. Intenta nuevamente en 30 minutos.");
    }
}

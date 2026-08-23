package com.cafepos.core.shared.seguridad;

/** PIN bloqueado tras 5 intentos fallidos consecutivos — ver Usuario.estaPinBloqueado(). */
public class PinBloqueadoException extends RuntimeException {

    public static final String CODIGO = "PIN_BLOQUEADO";

    public PinBloqueadoException() {
        super("PIN bloqueado por intentos fallidos. Intenta de nuevo en 30 minutos.");
    }
}

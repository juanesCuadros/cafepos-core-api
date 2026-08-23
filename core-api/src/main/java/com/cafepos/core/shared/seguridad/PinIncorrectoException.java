package com.cafepos.core.shared.seguridad;

/**
 * Mensaje generico a proposito, mismo criterio que CredencialesInvalidasException:
 * usuario_autoriza_correo inexistente, rol distinto de Admin/Jefe, sin PIN
 * configurado y PIN incorrecto usan el mismo mensaje — nunca revelar cual
 * de los cuatro fue (ver PinVerificarService).
 *
 * intentosRestantes es null en el caso generico de arriba (no aplica, no
 * hubo comparacion de PIN); trae un valor cuando el PIN si se comparo y
 * fallo, para que el frontend pueda avisar cuantos intentos quedan antes
 * del bloqueo.
 */
public class PinIncorrectoException extends RuntimeException {

    public static final String CODIGO = "PIN_INCORRECTO";

    private final Integer intentosRestantes;

    public PinIncorrectoException() {
        this(null);
    }

    public PinIncorrectoException(Integer intentosRestantes) {
        super("PIN incorrecto");
        this.intentosRestantes = intentosRestantes;
    }

    public Integer getIntentosRestantes() {
        return intentosRestantes;
    }
}

package com.cafepos.core.operacion.domain;

/** El usuario no tiene un turno abierto para cerrar — ver TurnoService.cerrar. */
public class TurnoNoActivoException extends RuntimeException {

    public TurnoNoActivoException() {
        super("No tienes un turno activo para cerrar");
    }
}

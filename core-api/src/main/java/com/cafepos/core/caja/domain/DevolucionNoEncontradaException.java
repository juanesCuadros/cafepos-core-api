package com.cafepos.core.caja.domain;

/** El id no existe, o no pertenece al tenant actual. */
public class DevolucionNoEncontradaException extends RuntimeException {

    public DevolucionNoEncontradaException() {
        super("Devolucion no encontrada");
    }
}

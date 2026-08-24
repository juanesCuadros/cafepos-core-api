package com.cafepos.core.caja.domain;

/** El id no existe, o no pertenece al tenant actual. */
public class FacturaNoEncontradaException extends RuntimeException {

    public FacturaNoEncontradaException() {
        super("Factura no encontrada");
    }
}

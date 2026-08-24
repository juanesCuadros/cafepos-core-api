package com.cafepos.core.caja.domain;

/** El id no existe, o no pertenece al tenant actual. */
public class VentaNoEncontradaException extends RuntimeException {

    public VentaNoEncontradaException() {
        super("Venta no encontrada");
    }
}

package com.cafepos.core.caja.domain;

/** La venta ya fue anulada (estado='anulado') — no se puede anular dos veces. */
public class VentaYaAnuladaException extends RuntimeException {

    public VentaYaAnuladaException() {
        super("Esta venta ya fue anulada");
    }
}

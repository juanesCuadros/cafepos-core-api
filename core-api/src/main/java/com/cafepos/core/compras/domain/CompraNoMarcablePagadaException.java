package com.cafepos.core.compras.domain;

/** 400 si forma_pago no es 'credito', o si estado ya es distinto de 'pendiente'. */
public class CompraNoMarcablePagadaException extends RuntimeException {

    public CompraNoMarcablePagadaException() {
        super("Solo se puede marcar como pagada una compra de credito en estado pendiente");
    }
}

package com.cafepos.core.caja.domain;

/** El pedido ya fue cobrado (estado='cerrado') — no se puede volver a cobrar. */
public class PedidoYaCerradoException extends RuntimeException {

    public PedidoYaCerradoException() {
        super("Este pedido ya fue cobrado");
    }
}

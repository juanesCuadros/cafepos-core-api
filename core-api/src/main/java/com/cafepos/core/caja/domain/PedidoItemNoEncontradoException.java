package com.cafepos.core.caja.domain;

/** El pedido_item_id de un item de POST /devoluciones no pertenece al pedido de la venta indicada. */
public class PedidoItemNoEncontradoException extends RuntimeException {

    public PedidoItemNoEncontradoException() {
        super("Item de pedido no encontrado en esta venta");
    }
}

package com.cafepos.core.operacion.domain;

/** El item no existe, o no pertenece al pedido/tenant indicado. */
public class PedidoItemNoEncontradoException extends RuntimeException {

    public PedidoItemNoEncontradoException() {
        super("Item no encontrado");
    }
}

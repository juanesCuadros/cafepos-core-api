package com.cafepos.core.operacion.domain;

/** El id no existe, o no pertenece al tenant actual. */
public class PedidoNoEncontradoException extends RuntimeException {

    public PedidoNoEncontradoException() {
        super("Pedido no encontrado");
    }
}

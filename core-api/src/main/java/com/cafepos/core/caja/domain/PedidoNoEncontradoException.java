package com.cafepos.core.caja.domain;

/** El pedido_id no existe, o no pertenece al tenant actual (ver operacion.PedidoService.buscarParaVenta via NamedInterface). */
public class PedidoNoEncontradoException extends RuntimeException {

    public PedidoNoEncontradoException() {
        super("Pedido no encontrado");
    }
}

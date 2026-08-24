package com.cafepos.core.operacion.domain;

/** Un pedido tipo='venta_rapida' no tiene mesa asociada — no aplica mover-mesa/marcar-lista-cobrar/prefactura. */
public class PedidoSinMesaException extends RuntimeException {

    public PedidoSinMesaException() {
        super("Este pedido no tiene mesa asociada");
    }
}

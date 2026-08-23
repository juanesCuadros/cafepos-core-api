package com.cafepos.core.operacion.domain;

/** La mesa ya tiene un pedido activo (estado != 'cerrado') — ver PedidoService.abrir. */
public class MesaOcupadaException extends RuntimeException {

    public MesaOcupadaException() {
        super("Esta mesa ya tiene un pedido abierto");
    }
}

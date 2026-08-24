package com.cafepos.core.operacion.domain;

/** tipo='venta_rapida' con mesa_id presente — ver PedidoService.abrir. */
public class MesaIdNoPermitidoException extends RuntimeException {

    public MesaIdNoPermitidoException() {
        super("mesa_id no debe enviarse para pedidos tipo 'venta_rapida'");
    }
}

package com.cafepos.core.operacion.domain;

/** tipo='mesa' sin mesa_id — ver PedidoService.abrir. */
public class MesaIdObligatorioException extends RuntimeException {

    public MesaIdObligatorioException() {
        super("mesa_id es obligatorio para pedidos tipo 'mesa'");
    }
}

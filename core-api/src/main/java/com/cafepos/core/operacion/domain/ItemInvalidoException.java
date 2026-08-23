package com.cafepos.core.operacion.domain;

/** El request de POST /pedidos/{id}/items no trajo exactamente uno de producto_id/combo_id. */
public class ItemInvalidoException extends RuntimeException {

    public ItemInvalidoException() {
        super("Debes indicar producto_id o combo_id (no ambos)");
    }
}

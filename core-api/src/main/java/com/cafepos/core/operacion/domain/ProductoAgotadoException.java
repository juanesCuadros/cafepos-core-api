package com.cafepos.core.operacion.domain;

/** producto.estado = 'agotado' — no se puede agregar al pedido. */
public class ProductoAgotadoException extends RuntimeException {

    public ProductoAgotadoException() {
        super("Este producto está agotado");
    }
}

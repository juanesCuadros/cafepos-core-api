package com.cafepos.core.productosmenu.domain;

/** aplica_a="producto" sin productos_ids (ausente o vacio) - una promocion de producto sin productos no tiene sentido. */
public class ProductosIdsRequeridosException extends RuntimeException {

    public ProductosIdsRequeridosException() {
        super("productos_ids es obligatorio y no puede estar vacío cuando aplica_a es 'producto'");
    }
}

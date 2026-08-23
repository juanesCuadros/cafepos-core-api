package com.cafepos.core.operacion.domain;

/** El producto_id no existe (ver productosmenu.ProductoService.buscarParaPedido via NamedInterface). */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException() {
        super("Producto no encontrado");
    }
}

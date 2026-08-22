package com.cafepos.core.productosmenu.domain;

/** DELETE /categorias/{id} rechazado — la categoria tiene productos asociados, ver CategoriaService.eliminar. */
public class CategoriaConProductosException extends RuntimeException {

    public CategoriaConProductosException(long numProductos) {
        super("No se puede eliminar, esta categoría tiene " + numProductos + " productos asociados");
    }
}

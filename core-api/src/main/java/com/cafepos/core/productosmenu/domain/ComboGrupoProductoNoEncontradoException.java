package com.cafepos.core.productosmenu.domain;

/** DELETE .../productos/{producto_id} rechazado — esa asociacion producto-grupo no existe. */
public class ComboGrupoProductoNoEncontradoException extends RuntimeException {

    public ComboGrupoProductoNoEncontradoException() {
        super("El producto no está en este grupo");
    }
}

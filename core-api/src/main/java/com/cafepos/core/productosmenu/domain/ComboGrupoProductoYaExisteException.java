package com.cafepos.core.productosmenu.domain;

/** POST .../productos rechazado — el producto ya esta en ESE grupo (UNIQUE(combo_grupo_id, producto_id)). */
public class ComboGrupoProductoYaExisteException extends RuntimeException {

    public ComboGrupoProductoYaExisteException() {
        super("El producto ya está en este grupo");
    }
}

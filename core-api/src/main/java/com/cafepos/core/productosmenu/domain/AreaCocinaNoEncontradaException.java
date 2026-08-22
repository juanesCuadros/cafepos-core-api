package com.cafepos.core.productosmenu.domain;

/** area_cocina_id enviado en POST/PATCH de producto que no existe, o no pertenece al tenant actual. */
public class AreaCocinaNoEncontradaException extends RuntimeException {

    public AreaCocinaNoEncontradaException() {
        super("Área de cocina no encontrada");
    }
}

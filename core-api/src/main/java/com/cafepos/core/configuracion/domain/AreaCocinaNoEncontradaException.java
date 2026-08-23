package com.cafepos.core.configuracion.domain;

public class AreaCocinaNoEncontradaException extends RuntimeException {

    public AreaCocinaNoEncontradaException() {
        super("Área de cocina no encontrada");
    }
}

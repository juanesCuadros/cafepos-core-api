package com.cafepos.core.configuracion.infrastructure.web;

public record AreaCocinaEliminadaResponse(String mensaje) {

    public static final AreaCocinaEliminadaResponse ELIMINADA = new AreaCocinaEliminadaResponse("Área de cocina eliminada");
}

package com.cafepos.core.restaurante.infrastructure.web;

public record ZonaEliminadaResponse(String mensaje) {

    public static final ZonaEliminadaResponse ELIMINADA = new ZonaEliminadaResponse("Zona eliminada");
}

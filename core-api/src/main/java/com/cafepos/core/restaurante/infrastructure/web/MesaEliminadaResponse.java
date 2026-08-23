package com.cafepos.core.restaurante.infrastructure.web;

public record MesaEliminadaResponse(String mensaje) {

    public static final MesaEliminadaResponse ELIMINADA = new MesaEliminadaResponse("Mesa eliminada");
}

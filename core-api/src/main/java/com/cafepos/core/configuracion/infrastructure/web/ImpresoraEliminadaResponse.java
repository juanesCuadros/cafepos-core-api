package com.cafepos.core.configuracion.infrastructure.web;

public record ImpresoraEliminadaResponse(String mensaje) {

    public static final ImpresoraEliminadaResponse ELIMINADA = new ImpresoraEliminadaResponse("Impresora eliminada");
}

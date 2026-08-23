package com.cafepos.core.restaurante.infrastructure.web;

public record MetodoPagoEliminadoResponse(String mensaje) {

    public static final MetodoPagoEliminadoResponse ELIMINADO =
            new MetodoPagoEliminadoResponse("Método de pago eliminado");
}

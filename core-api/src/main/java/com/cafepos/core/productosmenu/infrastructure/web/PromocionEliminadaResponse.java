package com.cafepos.core.productosmenu.infrastructure.web;

public record PromocionEliminadaResponse(String mensaje) {

    public static final PromocionEliminadaResponse ELIMINADA = new PromocionEliminadaResponse("Promoción eliminada");
}

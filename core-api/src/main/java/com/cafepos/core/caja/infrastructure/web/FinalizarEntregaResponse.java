package com.cafepos.core.caja.infrastructure.web;

public record FinalizarEntregaResponse(String mensaje, boolean mesaLiberada) {

    public static FinalizarEntregaResponse de(boolean mesaLiberada) {
        return new FinalizarEntregaResponse("Venta finalizada", mesaLiberada);
    }
}

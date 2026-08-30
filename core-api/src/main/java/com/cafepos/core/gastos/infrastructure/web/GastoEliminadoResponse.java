package com.cafepos.core.gastos.infrastructure.web;

public record GastoEliminadoResponse(String mensaje) {

    public static final GastoEliminadoResponse INSTANCIA = new GastoEliminadoResponse("Gasto eliminado");
}

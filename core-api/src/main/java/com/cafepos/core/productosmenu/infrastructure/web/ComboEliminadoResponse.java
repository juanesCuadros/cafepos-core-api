package com.cafepos.core.productosmenu.infrastructure.web;

public record ComboEliminadoResponse(String mensaje) {

    public static final ComboEliminadoResponse ELIMINADO = new ComboEliminadoResponse("Combo eliminado");
}

package com.cafepos.core.productosmenu.infrastructure.web;

public record CategoriaEliminadaResponse(String mensaje) {

    public static final CategoriaEliminadaResponse ELIMINADA = new CategoriaEliminadaResponse("Categoría eliminada");
}

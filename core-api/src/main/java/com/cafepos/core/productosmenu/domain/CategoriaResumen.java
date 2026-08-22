package com.cafepos.core.productosmenu.domain;

/** Fila de GET /categorias — numProductos es calculado (COUNT), no una columna de categoria. */
public record CategoriaResumen(Integer id, String icono, String nombre, Integer orden,
                                long numProductos, String estado) {
}

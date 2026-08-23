package com.cafepos.core.restaurante.domain;

import java.util.List;

/** Categoria con sus productos anidados en GET /menu-publico. */
public record MenuPublicoCategoria(String nombre, List<MenuPublicoProducto> productos) {
}

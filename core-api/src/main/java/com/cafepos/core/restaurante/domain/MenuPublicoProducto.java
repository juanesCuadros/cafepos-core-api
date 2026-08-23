package com.cafepos.core.restaurante.domain;

import java.math.BigDecimal;

/** Producto dentro de una categoria en GET /menu-publico. */
public record MenuPublicoProducto(String nombre, String descripcion, BigDecimal precioVenta, String imagen) {
}

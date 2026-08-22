package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;

/** Fila de GET /combos. */
public record ComboResumen(Integer id, String codigo, String nombre, BigDecimal precio, String estado) {
}

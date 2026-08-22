package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;

/** Fila de GET /productos — categoria viene aplanada (categoriaId + categoriaNombre), el join ya se hizo en SQL. */
public record ProductoResumen(Integer id, String codigo, String nombre, Integer categoriaId,
                               String categoriaNombre, String imagen, BigDecimal precioVenta, String estado) {
}

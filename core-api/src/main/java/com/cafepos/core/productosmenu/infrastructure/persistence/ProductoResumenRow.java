package com.cafepos.core.productosmenu.infrastructure.persistence;

import java.math.BigDecimal;

/** Proyeccion de la query nativa ProductoJpaRepository.listar — alias exactos de la columna. */
interface ProductoResumenRow {

    Integer getId();

    String getCodigo();

    String getNombre();

    Integer getCategoriaId();

    String getCategoriaNombre();

    String getImagen();

    BigDecimal getPrecioVenta();

    String getEstado();
}

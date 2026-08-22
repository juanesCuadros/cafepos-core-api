package com.cafepos.core.productosmenu.infrastructure.persistence;

import java.math.BigDecimal;

/** Proyeccion de la query nativa ComboJpaRepository.listar — alias exactos de la columna. */
interface ComboResumenRow {

    Integer getId();

    String getCodigo();

    String getNombre();

    BigDecimal getPrecio();

    String getEstado();
}

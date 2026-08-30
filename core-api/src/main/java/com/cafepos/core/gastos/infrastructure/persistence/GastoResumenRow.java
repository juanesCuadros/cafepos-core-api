package com.cafepos.core.gastos.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyeccion de la query nativa GastoJpaRepository.listar — alias exactos de la columna. */
interface GastoResumenRow {

    Integer getId();

    String getCodigo();

    LocalDate getFecha();

    String getCategoria();

    String getDescripcion();

    BigDecimal getMonto();

    String getMetodoPago();

    String getUsuario();
}

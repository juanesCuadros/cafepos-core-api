package com.cafepos.core.inventario.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyeccion de la query nativa PerdidaJpaRepository.listar — alias exactos de la columna. */
interface PerdidaResumenRow {

    Integer getId();

    LocalDate getFecha();

    String getInsumoNombre();

    BigDecimal getCantidad();

    String getMotivo();

    BigDecimal getCostoCalculado();

    String getUsuarioNombre();
}

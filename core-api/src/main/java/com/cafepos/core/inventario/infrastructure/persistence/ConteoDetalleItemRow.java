package com.cafepos.core.inventario.infrastructure.persistence;

import java.math.BigDecimal;

/** Proyeccion de la query nativa ConteoDetalleJpaRepository.detalleDe — alias exactos de la columna. */
interface ConteoDetalleItemRow {

    String getInsumoNombre();

    BigDecimal getStockSistema();

    BigDecimal getStockFisico();

    BigDecimal getDiferencia();
}

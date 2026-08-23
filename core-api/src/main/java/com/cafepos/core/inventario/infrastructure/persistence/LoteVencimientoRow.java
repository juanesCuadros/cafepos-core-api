package com.cafepos.core.inventario.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyeccion de la query nativa VencimientoJpaRepository.listar — alias exactos de la columna. */
interface LoteVencimientoRow {

    Integer getLoteId();

    Integer getInsumoId();

    String getInsumoCodigo();

    String getInsumoNombre();

    BigDecimal getStockActualInsumo();

    String getNumeroLote();

    LocalDate getFechaVencimiento();

    Long getDiasRestantes();

    String getEstadoCalc();
}

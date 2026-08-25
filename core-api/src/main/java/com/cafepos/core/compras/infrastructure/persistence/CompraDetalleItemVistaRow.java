package com.cafepos.core.compras.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyeccion de la query nativa CompraDetalleJpaRepository.listarVistaPorCompraId — alias exactos de la columna. */
interface CompraDetalleItemVistaRow {

    Integer getId();

    Integer getInsumoId();

    String getInsumoCodigo();

    String getInsumoNombre();

    String getUnidadMedida();

    BigDecimal getCantidad();

    BigDecimal getCostoUnitario();

    String getNumeroLote();

    LocalDate getFechaVencimiento();

    BigDecimal getSubtotal();
}

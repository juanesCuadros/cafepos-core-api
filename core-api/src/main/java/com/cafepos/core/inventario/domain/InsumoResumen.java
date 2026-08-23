package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila de GET /insumos — categoria viene aplanada, el join ya se hizo en
 * SQL. valorTotal y estadoStock ya vienen calculados desde la query
 * nativa (ver InsumoJpaRepository) para poder filtrar por estado_stock
 * ahi mismo, sin traer todo a memoria para filtrar en Java.
 */
public record InsumoResumen(Integer id, String codigo, String nombre, Integer categoriaInsumoId,
                             String categoriaInsumoNombre, String unidadMedida, BigDecimal stockActual,
                             BigDecimal stockMinimo, BigDecimal costoActual, BigDecimal valorTotal,
                             String estadoStock, String estado, OffsetDateTime fechaRegistro) {
}

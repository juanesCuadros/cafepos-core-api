package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila de GET /lotes-insumo/vencimientos — el lote mas urgente por insumo (ver VencimientoRepository). */
public record LoteVencimiento(Integer loteId, Integer insumoId, String insumoCodigo, String insumoNombre,
                               BigDecimal stockActualInsumo, String numeroLote, LocalDate fechaVencimiento,
                               long diasRestantes, String estado) {
}

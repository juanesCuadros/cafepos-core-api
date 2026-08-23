package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Fila de GET /movimientos-inventario — insumo y usuario ya vienen como nombre plano (join hecho en SQL). */
public record MovimientoInventarioResumen(Integer id, OffsetDateTime fechaHora, String insumoNombre, String tipo,
                                           BigDecimal cantidad, String unidadMedida, String usuarioNombre,
                                           String motivoOrigen, String referenciaTipo, Integer referenciaId) {
}

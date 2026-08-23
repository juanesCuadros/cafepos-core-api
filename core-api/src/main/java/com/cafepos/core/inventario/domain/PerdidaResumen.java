package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila de GET /perdidas — insumo y usuario ya vienen como nombre plano (join hecho en SQL). */
public record PerdidaResumen(Integer id, LocalDate fecha, String insumoNombre, BigDecimal cantidad, String motivo,
                              BigDecimal costoCalculado, String usuarioNombre) {
}

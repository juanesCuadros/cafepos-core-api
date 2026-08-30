package com.cafepos.core.gastos.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Fila de GET /gastos — categoria/usuario vienen aplanados (join ya hecho en SQL), ver GastoRepository.listar. */
public record GastoResumen(Integer id, String codigo, LocalDate fecha, String categoria, String descripcion,
                            BigDecimal monto, String metodoPago, String usuario) {
}

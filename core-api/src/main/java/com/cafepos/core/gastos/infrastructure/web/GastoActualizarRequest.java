package com.cafepos.core.gastos.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Mismos campos del POST, todos opcionales. comprobanteImagen/observaciones son JsonNullable (nullable de negocio, ver CLAUDE.md). */
public record GastoActualizarRequest(Integer categoriaGastoId, String descripcion, BigDecimal monto,
                                      String metodoPago, LocalDate fecha,
                                      JsonNullable<String> comprobanteImagen,
                                      JsonNullable<String> observaciones) {
}

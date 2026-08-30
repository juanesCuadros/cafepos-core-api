package com.cafepos.core.personal.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Fila de GET /turnos — empleado_nombre aplanado, el join ya se hizo en SQL. */
public record TurnoResumen(Integer id, String empleadoNombre, LocalDate fecha, OffsetDateTime horaInicio,
                            OffsetDateTime horaFin, BigDecimal horasTrabajadas) {
}

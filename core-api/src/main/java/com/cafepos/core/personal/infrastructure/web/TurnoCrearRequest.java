package com.cafepos.core.personal.infrastructure.web;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Sin horas_trabajadas a proposito — nunca se acepta del cliente, siempre se calcula (ver Turno.calcularHoras). */
public record TurnoCrearRequest(
        @NotNull(message = "empleado_id es obligatorio") Integer empleadoId,
        @NotNull(message = "fecha es obligatoria") LocalDate fecha,
        @NotNull(message = "hora_inicio es obligatoria") OffsetDateTime horaInicio,
        @NotNull(message = "hora_fin es obligatoria") OffsetDateTime horaFin,
        String observaciones) {
}

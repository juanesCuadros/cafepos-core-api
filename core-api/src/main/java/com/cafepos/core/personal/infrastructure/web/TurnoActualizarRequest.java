package com.cafepos.core.personal.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TurnoActualizarRequest(Integer empleadoId, LocalDate fecha, OffsetDateTime horaInicio,
                                      OffsetDateTime horaFin, JsonNullable<String> observaciones) {
}

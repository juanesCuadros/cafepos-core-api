package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.TurnoResumen;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TurnoListItemResponse(Integer id, Integer empleadoId, String empleado, LocalDate fecha,
                                     OffsetDateTime horaInicio, OffsetDateTime horaFin, BigDecimal horasTrabajadas,
                                     String observaciones) {

    public static TurnoListItemResponse de(TurnoResumen r) {
        return new TurnoListItemResponse(r.id(), r.empleadoId(), r.empleadoNombre(), r.fecha(), r.horaInicio(),
                r.horaFin(), r.horasTrabajadas(), r.observaciones());
    }
}

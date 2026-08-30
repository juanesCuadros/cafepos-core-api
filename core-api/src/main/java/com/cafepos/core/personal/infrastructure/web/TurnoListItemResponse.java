package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.TurnoResumen;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TurnoListItemResponse(Integer id, String empleado, LocalDate fecha, OffsetDateTime horaInicio,
                                     OffsetDateTime horaFin, BigDecimal horasTrabajadas) {

    public static TurnoListItemResponse de(TurnoResumen r) {
        return new TurnoListItemResponse(r.id(), r.empleadoNombre(), r.fecha(), r.horaInicio(), r.horaFin(),
                r.horasTrabajadas());
    }
}

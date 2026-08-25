package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.Turno;

import java.math.BigDecimal;

public record TurnoCreadoResponse(Integer id, Integer empleadoId, BigDecimal horasTrabajadas) {

    public static TurnoCreadoResponse de(Turno turno) {
        return new TurnoCreadoResponse(turno.getId(), turno.getEmpleadoId(), turno.getHorasTrabajadas());
    }
}

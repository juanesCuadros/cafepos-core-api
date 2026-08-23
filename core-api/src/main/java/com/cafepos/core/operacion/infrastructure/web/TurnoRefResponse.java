package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.domain.Turno;

import java.time.OffsetDateTime;

public record TurnoRefResponse(Integer id, OffsetDateTime horaInicio) {

    public static TurnoRefResponse de(Turno turno) {
        return new TurnoRefResponse(turno.getId(), turno.getHoraInicio());
    }
}

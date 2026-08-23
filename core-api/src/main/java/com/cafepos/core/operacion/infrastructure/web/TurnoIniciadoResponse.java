package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.domain.Turno;

import java.time.OffsetDateTime;

public record TurnoIniciadoResponse(Integer id, Integer empleadoId, OffsetDateTime horaInicio) {

    public static TurnoIniciadoResponse de(Turno turno) {
        return new TurnoIniciadoResponse(turno.getId(), turno.getEmpleadoId(), turno.getHoraInicio());
    }
}

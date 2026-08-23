package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.domain.Turno;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TurnoCerradoResponse(Integer id, OffsetDateTime horaInicio, OffsetDateTime horaFin,
                                    BigDecimal horasTrabajadas) {

    public static TurnoCerradoResponse de(Turno turno) {
        return new TurnoCerradoResponse(turno.getId(), turno.getHoraInicio(), turno.getHoraFin(),
                turno.getHorasTrabajadas());
    }
}

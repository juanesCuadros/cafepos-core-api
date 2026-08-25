package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.ResumenTurnosMes;

import java.math.BigDecimal;

public record ResumenTurnosMesResponse(long totalTurnos, BigDecimal horasTrabajadas) {

    public static ResumenTurnosMesResponse de(ResumenTurnosMes r) {
        return new ResumenTurnosMesResponse(r.totalTurnos(), r.horasTrabajadas());
    }
}

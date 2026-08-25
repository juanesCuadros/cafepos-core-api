package com.cafepos.core.personal.domain;

import java.math.BigDecimal;

/** GET /empleados/{id} — COUNT y SUM(horas_trabajadas) de turno para el mes calendario actual. */
public record ResumenTurnosMes(long totalTurnos, BigDecimal horasTrabajadas) {
}

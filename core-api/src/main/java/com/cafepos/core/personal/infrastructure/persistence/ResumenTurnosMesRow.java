package com.cafepos.core.personal.infrastructure.persistence;

import java.math.BigDecimal;

interface ResumenTurnosMesRow {

    Long getTotalTurnos();

    BigDecimal getHorasTrabajadas();
}

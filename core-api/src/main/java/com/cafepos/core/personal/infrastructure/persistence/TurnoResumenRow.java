package com.cafepos.core.personal.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** hora_inicio/hora_fin son TIMESTAMPTZ — getter Instant, nunca OffsetDateTime, en proyeccion nativa (ver CLAUDE.md). */
interface TurnoResumenRow {

    Integer getId();

    String getEmpleadoNombre();

    LocalDate getFecha();

    Instant getHoraInicio();

    Instant getHoraFin();

    BigDecimal getHorasTrabajadas();
}

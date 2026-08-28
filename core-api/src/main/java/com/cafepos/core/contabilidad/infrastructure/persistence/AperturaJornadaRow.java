package com.cafepos.core.contabilidad.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/** Instant, no OffsetDateTime — proyeccion nativa sobre columna TIMESTAMPTZ (ver CLAUDE.md). */
interface AperturaJornadaRow {

    Instant getFechaApertura();

    BigDecimal getMontoInicial();
}

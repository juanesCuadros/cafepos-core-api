package com.cafepos.core.personal.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/** venta.fecha_hora es TIMESTAMPTZ — getter Instant, nunca OffsetDateTime, en proyeccion nativa (ver CLAUDE.md). */
interface VentaConPropinaRow {

    String getCodigo();

    Instant getFecha();

    BigDecimal getPropina();
}

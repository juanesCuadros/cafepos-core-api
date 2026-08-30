package com.cafepos.core.contabilidad.domain;

import java.time.LocalDate;

/** Rango ya resuelto (ver ResolverRangoFechas) — nunca null en ninguno de los dos campos una vez resuelto. */
public record RangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
}

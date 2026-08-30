package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Fila cruda de un gasto. fechaHora = gasto.created_at, mismo criterio que CompraContable. */
public record GastoContable(OffsetDateTime fechaHora, String codigo, BigDecimal monto, String descripcion,
                             String metodoPago, String usuarioNombre) {
}

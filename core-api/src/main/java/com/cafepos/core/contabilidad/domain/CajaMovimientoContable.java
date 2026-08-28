package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CajaMovimientoContable(OffsetDateTime fechaHora, BigDecimal monto, String motivo,
                                      String usuarioNombre) {
}

package com.cafepos.core.personal.application;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DetallePropinaVenta(String ventaCodigo, OffsetDateTime fecha, BigDecimal propinaTotalVenta,
                                   BigDecimal montoAtribuido) {
}

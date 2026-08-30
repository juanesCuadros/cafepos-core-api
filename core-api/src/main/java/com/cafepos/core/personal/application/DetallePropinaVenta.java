package com.cafepos.core.personal.application;

import org.springframework.modulith.NamedInterface;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@NamedInterface
public record DetallePropinaVenta(String ventaCodigo, OffsetDateTime fecha, BigDecimal propinaTotalVenta,
                                   BigDecimal montoAtribuido) {
}

package com.cafepos.core.personal.application;

import org.springframework.modulith.NamedInterface;
import java.math.BigDecimal;
import java.util.List;

@NamedInterface
public record ResumenPropinas(BigDecimal totalPropinas, List<DetallePropinaVenta> detalle) {
}

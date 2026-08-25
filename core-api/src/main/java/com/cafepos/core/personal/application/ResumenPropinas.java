package com.cafepos.core.personal.application;

import java.math.BigDecimal;
import java.util.List;

public record ResumenPropinas(BigDecimal totalPropinas, List<DetallePropinaVenta> detalle) {
}

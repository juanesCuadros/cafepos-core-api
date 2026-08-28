package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;

public record ReporteVentasTotales(@Monto BigDecimal totalVentas, Long numTransacciones) {}

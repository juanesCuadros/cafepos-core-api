package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;
import java.util.List;

public record ReporteVentasResponse(List<ReporteVentasFila> filas, ReporteVentasTotales totales) {}

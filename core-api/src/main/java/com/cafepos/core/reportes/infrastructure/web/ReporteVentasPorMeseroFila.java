package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;

public record ReporteVentasPorMeseroFila(String mesero, Long numPedidos, @Monto BigDecimal totalVentas, @Monto BigDecimal ticketPromedio, @Monto BigDecimal propinasRecibidas) {}

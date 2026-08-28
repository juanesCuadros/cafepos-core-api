package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteVentasFila(LocalDate fecha, @Monto BigDecimal totalVentas, Long numTransacciones, @Monto BigDecimal ticketPromedio, String comparativaPeriodoAnterior) {}

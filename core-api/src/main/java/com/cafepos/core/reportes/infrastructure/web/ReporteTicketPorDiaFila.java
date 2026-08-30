package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteTicketPorDiaFila(LocalDate fecha, Long numTransacciones, @Monto BigDecimal totalVentas, @Monto BigDecimal ticketPromedio, @Monto BigDecimal ticketMasAlto, @Monto BigDecimal ticketMasBajo) {}

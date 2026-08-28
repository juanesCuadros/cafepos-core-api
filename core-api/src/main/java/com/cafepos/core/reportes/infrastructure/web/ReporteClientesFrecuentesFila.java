package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteClientesFrecuentesFila(Integer posicion, String cliente, Long numVisitas, @Monto BigDecimal totalGastado, @Monto BigDecimal ticketPromedio, LocalDate ultimaVisita) {}

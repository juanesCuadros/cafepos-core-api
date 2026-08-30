package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReporteDemandaFila(String dia, String hora, Long numPedidos, @Monto BigDecimal totalVentas, @Monto BigDecimal ticketPromedio) {}

package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;

public record ReporteProductosMasVendidosFila(Integer posicion, String producto, String categoria, BigDecimal unidadesVendidas, @Monto BigDecimal totalVentas, String porcentajeTotal) {}

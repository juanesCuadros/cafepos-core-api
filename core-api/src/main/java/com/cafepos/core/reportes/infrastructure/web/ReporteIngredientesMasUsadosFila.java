package com.cafepos.core.reportes.infrastructure.web;
import com.cafepos.core.shared.jackson.Monto;
import java.math.BigDecimal;

public record ReporteIngredientesMasUsadosFila(Integer posicion, String ingrediente, String categoria, BigDecimal cantidadUsada, String unidadMedida, @Monto BigDecimal costoTotalConsumido) {}

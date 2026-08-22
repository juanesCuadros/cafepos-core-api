package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PromocionCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "tipo_descuento es obligatorio")
        @Pattern(regexp = "porcentaje|valor_fijo", message = "tipo_descuento debe ser 'porcentaje' o 'valor_fijo'")
        String tipoDescuento,

        @NotNull(message = "valor_descuento es obligatorio")
        BigDecimal valorDescuento,

        @NotBlank(message = "aplica_a es obligatorio")
        @Pattern(regexp = "producto|venta_total", message = "aplica_a debe ser 'producto' o 'venta_total'")
        String aplicaA,

        List<Integer> productosIds,

        @NotNull(message = "vigencia_inicio es obligatorio")
        LocalDate vigenciaInicio,

        @NotNull(message = "vigencia_fin es obligatorio")
        LocalDate vigenciaFin,

        List<String> diasSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cantidadMinima,
        BigDecimal montoMinimo,
        String estado) {
}

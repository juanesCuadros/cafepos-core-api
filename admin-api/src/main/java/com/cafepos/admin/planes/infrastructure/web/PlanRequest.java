package com.cafepos.admin.planes.infrastructure.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Mismos campos para crear y editar. limiteUsuarios null = ilimitado. */
public record PlanRequest(
        @NotBlank(message = "El nombre del plan es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "El precio mensual es obligatorio")
        @DecimalMin(value = "0", message = "El precio mensual no puede ser negativo")
        BigDecimal precioMensual,

        Integer limiteUsuarios,

        @NotNull(message = "Los días de prueba son obligatorios")
        @Min(value = 0, message = "Los días de prueba no pueden ser negativos")
        Integer diasPrueba) {
}

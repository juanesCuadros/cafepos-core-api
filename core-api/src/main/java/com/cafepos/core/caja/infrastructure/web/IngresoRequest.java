package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record IngresoRequest(
        @NotNull(message = "monto es obligatorio")
        @Positive(message = "monto debe ser mayor a 0")
        BigDecimal monto,

        @NotBlank(message = "motivo es obligatorio")
        String motivo) {
}

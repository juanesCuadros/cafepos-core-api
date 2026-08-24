package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record JornadaAbrirRequest(
        @NotNull(message = "monto_inicial es obligatorio")
        @PositiveOrZero(message = "monto_inicial no puede ser negativo")
        BigDecimal montoInicial) {
}

package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CerrarJornadaRequest(
        @NotNull(message = "monto_final_fisico es obligatorio")
        @PositiveOrZero(message = "monto_final_fisico no puede ser negativo")
        BigDecimal montoFinalFisico) {
}

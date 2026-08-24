package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PagoRequest(
        @NotNull(message = "metodo_pago_id es obligatorio") Integer metodoPagoId,
        @NotNull(message = "monto es obligatorio")
        @Positive(message = "monto debe ser mayor a 0") BigDecimal monto) {
}

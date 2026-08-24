package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PromocionAplicadaRequest(
        @NotNull(message = "promocion_id es obligatorio") Integer promocionId,
        @NotNull(message = "monto_descuento es obligatorio") BigDecimal montoDescuento) {
}

package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemDevolucionRequest(
        @NotNull(message = "pedidoItemId es obligatorio") Integer pedidoItemId,
        @NotNull(message = "cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "cantidad debe ser mayor a cero") BigDecimal cantidad) {
}

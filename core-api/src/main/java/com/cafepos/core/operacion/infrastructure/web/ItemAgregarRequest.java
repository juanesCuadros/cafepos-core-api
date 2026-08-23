package com.cafepos.core.operacion.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/** producto_id O combo_id (no ambos) — validado en PedidoService.agregarItem, no aca (mensaje generico no aplica bien a bean validation cruzada). */
public record ItemAgregarRequest(
        Integer productoId,
        Integer comboId,

        @NotNull(message = "cantidad es obligatoria")
        @Positive(message = "cantidad debe ser mayor a 0")
        BigDecimal cantidad,

        String observacion,

        List<SeleccionComboRequest> selecciones) {
}

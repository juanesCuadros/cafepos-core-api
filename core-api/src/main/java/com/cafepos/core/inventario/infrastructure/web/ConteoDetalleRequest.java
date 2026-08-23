package com.cafepos.core.inventario.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** Item de "detalle" dentro de POST /conteos. */
public record ConteoDetalleRequest(
        @NotNull(message = "insumo_id es obligatorio")
        Integer insumoId,

        @NotNull(message = "stock_fisico es obligatorio")
        @PositiveOrZero(message = "stock_fisico no puede ser negativo")
        BigDecimal stockFisico) {
}

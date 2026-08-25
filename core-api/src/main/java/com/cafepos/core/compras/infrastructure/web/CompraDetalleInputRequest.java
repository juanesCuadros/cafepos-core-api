package com.cafepos.core.compras.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompraDetalleInputRequest(
        @NotNull(message = "insumo_id es obligatorio") Integer insumoId,
        @NotNull(message = "cantidad es obligatoria")
        @Positive(message = "cantidad debe ser mayor a 0") BigDecimal cantidad,
        @NotNull(message = "costo_unitario es obligatorio")
        @Positive(message = "costo_unitario debe ser mayor a 0") BigDecimal costoUnitario,
        @NotBlank(message = "numero_lote es obligatorio") String numeroLote,
        @NotNull(message = "fecha_vencimiento es obligatoria") LocalDate fechaVencimiento) {
}

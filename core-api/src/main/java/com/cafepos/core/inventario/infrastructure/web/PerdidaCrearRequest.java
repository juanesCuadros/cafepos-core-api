package com.cafepos.core.inventario.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PerdidaCrearRequest(
        @NotNull(message = "insumo_id es obligatorio")
        Integer insumoId,

        @NotNull(message = "cantidad es obligatoria")
        @Positive(message = "cantidad debe ser mayor a 0")
        BigDecimal cantidad,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @NotNull(message = "fecha es obligatoria")
        LocalDate fecha,

        String observaciones) {
}

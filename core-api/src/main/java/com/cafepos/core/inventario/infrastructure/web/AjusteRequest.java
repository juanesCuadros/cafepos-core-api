package com.cafepos.core.inventario.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AjusteRequest(
        @NotNull(message = "insumo_id es obligatorio")
        Integer insumoId,

        @NotBlank(message = "tipo es obligatorio")
        @Pattern(regexp = "entrada|salida", message = "tipo debe ser 'entrada' o 'salida'")
        String tipo,

        @NotNull(message = "cantidad es obligatoria")
        @Positive(message = "cantidad debe ser mayor a 0")
        BigDecimal cantidad,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @NotNull(message = "usuario_autoriza_id es obligatorio")
        Integer usuarioAutorizaId) {
}

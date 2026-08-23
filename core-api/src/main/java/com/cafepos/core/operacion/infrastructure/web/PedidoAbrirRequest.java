package com.cafepos.core.operacion.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PedidoAbrirRequest(
        @NotNull(message = "mesa_id es obligatorio")
        Integer mesaId,

        @NotBlank(message = "tipo es obligatorio")
        @Pattern(regexp = "mesa", message = "tipo debe ser 'mesa' (venta_rapida vive en Caja, fuera de alcance aca)")
        String tipo) {
}

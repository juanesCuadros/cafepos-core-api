package com.cafepos.core.operacion.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record KdsItemEstadoRequest(
        @NotBlank(message = "estado_preparacion es obligatorio")
        @Pattern(regexp = "pendiente|en_preparacion|listo",
                message = "estado_preparacion debe ser 'pendiente', 'en_preparacion' o 'listo'")
        String estadoPreparacion) {
}

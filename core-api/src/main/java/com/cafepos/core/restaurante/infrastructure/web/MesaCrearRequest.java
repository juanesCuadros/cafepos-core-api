package com.cafepos.core.restaurante.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record MesaCrearRequest(
        @NotBlank(message = "El numero es obligatorio")
        String numero,

        @Positive(message = "capacidad debe ser mayor a 0")
        Integer capacidad,

        String estado) {
}

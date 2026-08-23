package com.cafepos.core.configuracion.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record AreaCocinaCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El estado es obligatorio")
        String estado) {
}

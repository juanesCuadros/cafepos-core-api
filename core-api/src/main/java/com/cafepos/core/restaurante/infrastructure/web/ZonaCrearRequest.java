package com.cafepos.core.restaurante.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record ZonaCrearRequest(
        String icono,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String estado) {
}

package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record CategoriaCrearRequest(
        String icono,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,
        Integer orden,
        String estado) {
}

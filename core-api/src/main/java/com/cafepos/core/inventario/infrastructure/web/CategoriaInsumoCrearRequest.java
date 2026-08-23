package com.cafepos.core.inventario.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record CategoriaInsumoCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre) {
}

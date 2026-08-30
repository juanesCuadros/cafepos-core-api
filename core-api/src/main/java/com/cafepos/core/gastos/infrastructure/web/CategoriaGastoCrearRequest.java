package com.cafepos.core.gastos.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record CategoriaGastoCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre) {
}

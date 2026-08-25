package com.cafepos.core.personal.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record EmpleadoCrearRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "La cedula es obligatoria") String cedula,
        String cargo,
        String telefono,
        String estado) {
}

package com.cafepos.core.configuracion.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @NotNull(message = "rol_id es obligatorio")
        Integer rolId,

        Integer empleadoId,
        String pin,

        @NotBlank(message = "El estado es obligatorio")
        String estado) {
}

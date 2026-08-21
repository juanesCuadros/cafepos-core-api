package com.cafepos.admin.negocios.infrastructure.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NegocioRequest(
        @NotBlank(message = "El nombre del negocio es obligatorio")
        String nombreNegocio,

        @NotBlank(message = "El identificador (slug) es obligatorio")
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
                message = "El identificador solo puede tener minúsculas, números y guiones, sin espacios")
        String slug,

        @NotNull(message = "El plan es obligatorio")
        Integer planId,

        @NotBlank(message = "El correo del Jefe es obligatorio")
        @Email(message = "El correo del Jefe no es válido")
        String correoJefe,

        @NotBlank(message = "El nombre del Jefe es obligatorio")
        String nombreJefe) {
}

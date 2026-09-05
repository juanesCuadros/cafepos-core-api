package com.cafepos.admin.negocios.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record EditarNegocioRequest(
        @NotBlank(message = "El nombre del negocio es obligatorio")
        String nombreNegocio,
        String nit,
        String direccion,
        String departamento,
        String ciudad,
        String telefono,
        String correo
) {
}

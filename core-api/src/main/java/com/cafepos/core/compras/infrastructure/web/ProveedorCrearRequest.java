package com.cafepos.core.compras.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record ProveedorCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String nit,
        String contacto,
        String telefono,
        String correo,
        String direccion,
        String estado) {
}

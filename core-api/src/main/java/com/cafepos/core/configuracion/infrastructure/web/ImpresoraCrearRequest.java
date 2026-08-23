package com.cafepos.core.configuracion.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record ImpresoraCrearRequest(
        Integer areaCocinaId,

        @NotBlank(message = "tipo es obligatorio")
        String tipo,

        String nombre,

        @NotBlank(message = "tipo_conexion es obligatorio")
        String tipoConexion,

        String ip,
        Integer puerto) {
}

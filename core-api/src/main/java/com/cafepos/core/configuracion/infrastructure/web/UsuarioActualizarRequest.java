package com.cafepos.core.configuracion.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

/** Mismos campos del POST excepto password — no se puede cambiar por aca. empleadoId y pin son JsonNullable. */
public record UsuarioActualizarRequest(
        String nombre,
        String correo,
        Integer rolId,
        JsonNullable<Integer> empleadoId,
        JsonNullable<String> pin,
        String estado) {
}

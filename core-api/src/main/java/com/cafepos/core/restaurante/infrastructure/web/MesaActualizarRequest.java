package com.cafepos.core.restaurante.infrastructure.web;

import jakarta.validation.constraints.Positive;

/**
 * Mismos campos que MesaCrearRequest mas zonaId (permite mover la mesa de
 * zona), todos opcionales. Ninguno usa JsonNullable — ningun campo de
 * mesa acepta null como valor valido en la tabla (ver Mesa.actualizar).
 */
public record MesaActualizarRequest(
        Integer zonaId,
        String numero,

        @Positive(message = "capacidad debe ser mayor a 0")
        Integer capacidad,

        String estado) {
}

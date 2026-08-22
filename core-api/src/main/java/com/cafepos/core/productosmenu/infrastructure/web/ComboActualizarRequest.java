package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.Positive;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

/**
 * Datos generales de PATCH /combos/{id} — a proposito NO tiene un campo
 * "grupos": si el body lo incluye, Jackson lo ignora silenciosamente (no
 * hay propiedad que lo reciba, y el proyecto no falla ante propiedades
 * desconocidas). Los grupos se editan solo por los sub-endpoints dedicados
 * (ver ComboController). descripcion/imagen son JsonNullable: ausente = no
 * tocar, null explicito = borrar (ver regla de DTOs de PATCH en CLAUDE.md).
 */
public record ComboActualizarRequest(
        String nombre,
        JsonNullable<String> descripcion,
        JsonNullable<String> imagen,

        @Positive(message = "precio debe ser mayor a 0")
        BigDecimal precio,

        String estado) {
}

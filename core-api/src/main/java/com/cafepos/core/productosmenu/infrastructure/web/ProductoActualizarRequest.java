package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.Positive;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

/**
 * Mismos campos que ProductoCrearRequest, todos opcionales — PATCH actualiza
 * solo lo que viene en el body. Los campos genuinamente nullable de negocio
 * (descripcion, costoEstimado, areaCocinaId, tasaImpuesto, unidadMedida,
 * stockMinimo, imagen) son JsonNullable: ausente = no tocar, null explicito =
 * borrar (ver regla de DTOs de PATCH en CLAUDE.md). El resto (nombre,
 * precioVenta, estado, ...) son obligatorios — null ahi sigue siendo error
 * de validacion, no "borrar".
 */
public record ProductoActualizarRequest(
        String nombre,
        JsonNullable<String> descripcion,
        Integer categoriaId,
        JsonNullable<String> imagen,

        @Positive(message = "precio_venta debe ser mayor a 0")
        BigDecimal precioVenta,

        JsonNullable<BigDecimal> costoEstimado,
        JsonNullable<Integer> areaCocinaId,
        JsonNullable<String> tasaImpuesto,
        Boolean manejaReceta,
        Boolean manejaInventario,
        JsonNullable<String> unidadMedida,
        JsonNullable<BigDecimal> stockMinimo,
        String estado,
        String visibilidad) {
}

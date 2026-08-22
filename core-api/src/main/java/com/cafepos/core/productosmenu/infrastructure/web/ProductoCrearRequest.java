package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductoCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "categoria_id es obligatorio")
        Integer categoriaId,

        String imagen,

        @NotNull(message = "precio_venta es obligatorio")
        @Positive(message = "precio_venta debe ser mayor a 0")
        BigDecimal precioVenta,

        BigDecimal costoEstimado,
        Integer areaCocinaId,
        String tasaImpuesto,
        Boolean manejaReceta,
        Boolean manejaInventario,
        String unidadMedida,
        BigDecimal stockMinimo,
        String estado,
        String visibilidad) {
}

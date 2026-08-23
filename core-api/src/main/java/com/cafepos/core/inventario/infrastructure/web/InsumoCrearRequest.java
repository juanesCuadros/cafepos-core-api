package com.cafepos.core.inventario.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Sin stock_actual ni costo_actual a proposito — se crean siempre en 0, el request los ignora aunque los mande. */
public record InsumoCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "categoria_insumo_id es obligatorio")
        Integer categoriaInsumoId,

        @NotBlank(message = "unidad_medida es obligatoria")
        String unidadMedida,

        BigDecimal stockMinimo,
        BigDecimal stockMaximo,
        LocalDate fechaVencimRef,
        String estado) {
}

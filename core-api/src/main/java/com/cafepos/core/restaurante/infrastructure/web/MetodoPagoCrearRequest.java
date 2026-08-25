package com.cafepos.core.restaurante.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

/** Sin es_efectivo a proposito — no es un campo que la API pueda setear (ver MetodoPago). */
public record MetodoPagoCrearRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String icono,
        String estado,
        String codigoFactus) {
}

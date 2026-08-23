package com.cafepos.core.restaurante.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

/** Mismos campos que MetodoPagoCrearRequest, todos opcionales. icono es JsonNullable (unico nullable en la tabla). */
public record MetodoPagoActualizarRequest(JsonNullable<String> icono, String nombre, String estado) {
}

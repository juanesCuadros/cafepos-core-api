package com.cafepos.core.restaurante.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

/** Mismos campos que ZonaCrearRequest, todos opcionales. icono es JsonNullable (unico nullable en la tabla zona). */
public record ZonaActualizarRequest(JsonNullable<String> icono, String nombre, String estado) {
}

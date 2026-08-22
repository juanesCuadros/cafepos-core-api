package com.cafepos.core.productosmenu.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Mismos campos que CategoriaCrearRequest, todos opcionales — PATCH actualiza
 * solo lo que viene en el body. descripcion es JsonNullable: ausente = no
 * tocar, null explicito = borrar (ver regla de DTOs de PATCH en CLAUDE.md).
 */
public record CategoriaActualizarRequest(String icono, String nombre, JsonNullable<String> descripcion,
                                          Integer orden, String estado) {
}

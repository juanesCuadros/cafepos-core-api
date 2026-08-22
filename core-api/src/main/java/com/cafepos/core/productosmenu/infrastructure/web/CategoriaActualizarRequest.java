package com.cafepos.core.productosmenu.infrastructure.web;

/** Mismos campos que CategoriaCrearRequest, todos opcionales — PATCH actualiza solo lo que viene en el body. */
public record CategoriaActualizarRequest(String icono, String nombre, String descripcion,
                                          Integer orden, String estado) {
}

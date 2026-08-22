package com.cafepos.core.productosmenu.infrastructure.web;

/** Forma anidada de "categoria" dentro de GET /productos - solo id+nombre, no la entidad completa. */
public record CategoriaRefResponse(Integer id, String nombre) {
}

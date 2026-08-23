package com.cafepos.core.inventario.infrastructure.web;

/** Forma anidada de "categoria" dentro de GET /insumos — solo id+nombre, no la entidad completa. */
public record CategoriaInsumoRefResponse(Integer id, String nombre) {
}

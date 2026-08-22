package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.Categoria;

/** Respuesta minima de POST/PATCH /categorias — solo id y nombre, asi lo especifica el contrato. */
public record CategoriaResponse(Integer id, String nombre) {

    public static CategoriaResponse de(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre());
    }
}

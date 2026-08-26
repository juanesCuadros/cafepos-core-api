package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.domain.CategoriaGasto;

/** POST /categorias-gasto — {"id","nombre"}, sin estado (nace siempre activa). */
public record CategoriaGastoCreadaResponse(Integer id, String nombre) {

    public static CategoriaGastoCreadaResponse de(CategoriaGasto categoriaGasto) {
        return new CategoriaGastoCreadaResponse(categoriaGasto.getId(), categoriaGasto.getNombre());
    }
}

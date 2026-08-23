package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.CategoriaInsumo;

public record CategoriaInsumoResponse(Integer id, String nombre) {

    public static CategoriaInsumoResponse de(CategoriaInsumo categoriaInsumo) {
        return new CategoriaInsumoResponse(categoriaInsumo.getId(), categoriaInsumo.getNombre());
    }
}

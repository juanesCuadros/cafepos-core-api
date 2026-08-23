package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.CategoriaInsumo;

import java.util.List;

public record CategoriasInsumoResponse(List<CategoriaInsumoResponse> categoriasInsumo) {

    public static CategoriasInsumoResponse de(List<CategoriaInsumo> categorias) {
        return new CategoriasInsumoResponse(categorias.stream().map(CategoriaInsumoResponse::de).toList());
    }
}

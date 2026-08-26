package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.domain.CategoriaGasto;

import java.util.List;

public record CategoriasGastoResponse(List<CategoriaGastoItemResponse> categorias) {

    public static CategoriasGastoResponse de(List<CategoriaGasto> categorias) {
        return new CategoriasGastoResponse(categorias.stream().map(CategoriaGastoItemResponse::de).toList());
    }
}

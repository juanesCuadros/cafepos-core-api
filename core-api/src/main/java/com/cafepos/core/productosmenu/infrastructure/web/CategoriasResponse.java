package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.CategoriaResumen;

import java.util.List;

public record CategoriasResponse(List<CategoriaListItemResponse> categorias) {

    public static CategoriasResponse de(List<CategoriaResumen> resumenes) {
        return new CategoriasResponse(resumenes.stream().map(CategoriaListItemResponse::de).toList());
    }
}

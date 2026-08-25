package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.CompraListadoItem;

import java.util.List;

public record ComprasResponse(List<CompraListItemResponse> compras) {

    public static ComprasResponse de(List<CompraListadoItem> lista) {
        return new ComprasResponse(lista.stream().map(CompraListItemResponse::de).toList());
    }
}

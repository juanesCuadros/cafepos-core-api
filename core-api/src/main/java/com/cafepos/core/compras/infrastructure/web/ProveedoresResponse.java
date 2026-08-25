package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.ProveedorResumen;

import java.util.List;

public record ProveedoresResponse(List<ProveedorListItemResponse> proveedores) {

    public static ProveedoresResponse de(List<ProveedorResumen> lista) {
        return new ProveedoresResponse(lista.stream().map(ProveedorListItemResponse::de).toList());
    }
}

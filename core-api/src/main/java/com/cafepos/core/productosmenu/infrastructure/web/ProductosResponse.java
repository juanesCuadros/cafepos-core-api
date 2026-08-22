package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.ProductoResumen;

import java.util.List;

public record ProductosResponse(List<ProductoListItemResponse> productos) {

    public static ProductosResponse de(List<ProductoResumen> resumenes) {
        return new ProductosResponse(resumenes.stream().map(ProductoListItemResponse::de).toList());
    }
}

package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.ProductoResumen;

import java.math.BigDecimal;

public record ProductoListItemResponse(Integer id, String codigo, String nombre, CategoriaRefResponse categoria,
                                        String imagen, BigDecimal precioVenta, String estado) {

    public static ProductoListItemResponse de(ProductoResumen resumen) {
        return new ProductoListItemResponse(resumen.id(), resumen.codigo(), resumen.nombre(),
                new CategoriaRefResponse(resumen.categoriaId(), resumen.categoriaNombre()),
                resumen.imagen(), resumen.precioVenta(), resumen.estado());
    }
}

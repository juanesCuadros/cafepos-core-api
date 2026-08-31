package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.CategoriaResumen;

public record CategoriaListItemResponse(Integer id, String icono, String nombre, String descripcion, Integer orden,
                                         long numProductos, String estado) {

    public static CategoriaListItemResponse de(CategoriaResumen resumen) {
        return new CategoriaListItemResponse(resumen.id(), resumen.icono(), resumen.nombre(), resumen.descripcion(),
                resumen.orden(), resumen.numProductos(), resumen.estado());
    }
}

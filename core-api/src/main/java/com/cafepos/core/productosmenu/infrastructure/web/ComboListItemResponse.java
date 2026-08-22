package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.ComboResumen;

import java.math.BigDecimal;

public record ComboListItemResponse(Integer id, String codigo, String nombre, BigDecimal precio, String estado) {

    public static ComboListItemResponse de(ComboResumen resumen) {
        return new ComboListItemResponse(resumen.id(), resumen.codigo(), resumen.nombre(), resumen.precio(),
                resumen.estado());
    }
}

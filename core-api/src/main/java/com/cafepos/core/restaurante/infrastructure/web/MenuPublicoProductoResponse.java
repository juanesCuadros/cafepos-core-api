package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MenuPublicoProducto;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record MenuPublicoProductoResponse(String nombre, String descripcion, @Monto BigDecimal precioVenta,
                                           String imagen) {

    public static MenuPublicoProductoResponse de(MenuPublicoProducto p) {
        return new MenuPublicoProductoResponse(p.nombre(), p.descripcion(), p.precioVenta(), p.imagen());
    }
}

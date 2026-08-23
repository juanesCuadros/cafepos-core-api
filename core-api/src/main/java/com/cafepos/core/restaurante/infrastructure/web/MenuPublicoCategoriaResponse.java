package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MenuPublicoCategoria;

import java.util.List;

public record MenuPublicoCategoriaResponse(String nombre, List<MenuPublicoProductoResponse> productos) {

    public static MenuPublicoCategoriaResponse de(MenuPublicoCategoria c) {
        return new MenuPublicoCategoriaResponse(c.nombre(),
                c.productos().stream().map(MenuPublicoProductoResponse::de).toList());
    }
}

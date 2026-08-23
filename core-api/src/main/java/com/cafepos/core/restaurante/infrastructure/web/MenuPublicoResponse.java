package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MenuPublicoVista;

import java.util.List;

public record MenuPublicoResponse(MenuPublicoRestauranteResponse restaurante,
                                   List<MenuPublicoCategoriaResponse> categorias) {

    public static MenuPublicoResponse de(MenuPublicoVista vista) {
        return new MenuPublicoResponse(
                new MenuPublicoRestauranteResponse(vista.restauranteNombre(), vista.restauranteLogoUrl()),
                vista.categorias().stream().map(MenuPublicoCategoriaResponse::de).toList());
    }
}

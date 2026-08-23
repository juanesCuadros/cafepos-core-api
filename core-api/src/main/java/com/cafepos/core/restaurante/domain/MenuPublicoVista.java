package com.cafepos.core.restaurante.domain;

import java.util.List;

/** GET /menu-publico — ver MenuPublicoService (404 "Menu no disponible" cuando esta vacio en el service). */
public record MenuPublicoVista(String restauranteNombre, String restauranteLogoUrl,
                                List<MenuPublicoCategoria> categorias) {
}

package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/** Item de "grupos" dentro de POST /combos — nombre + productos_ids ya resueltos por id. */
public record ComboGrupoRequest(
        @NotBlank(message = "El nombre del grupo es obligatorio")
        String nombre,

        List<Integer> productosIds) {
}

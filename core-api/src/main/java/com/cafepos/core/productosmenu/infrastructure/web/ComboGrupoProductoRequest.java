package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.NotNull;

/** Body de POST /combos/{id}/grupos/{grupo_id}/productos. */
public record ComboGrupoProductoRequest(
        @NotNull(message = "producto_id es obligatorio")
        Integer productoId) {
}

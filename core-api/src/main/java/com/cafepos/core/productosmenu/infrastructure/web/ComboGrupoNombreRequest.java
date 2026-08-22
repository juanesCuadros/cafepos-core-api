package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

/** Body de POST /combos/{id}/grupos y PATCH /combos/{id}/grupos/{grupo_id} — crear o renombrar un grupo. */
public record ComboGrupoNombreRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre) {
}

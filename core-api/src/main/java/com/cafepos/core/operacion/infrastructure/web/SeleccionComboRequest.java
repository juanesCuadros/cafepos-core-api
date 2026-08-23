package com.cafepos.core.operacion.infrastructure.web;

import jakarta.validation.constraints.NotNull;

public record SeleccionComboRequest(
        @NotNull(message = "combo_grupo_id es obligatorio") Integer comboGrupoId,
        @NotNull(message = "producto_id es obligatorio") Integer productoId) {
}

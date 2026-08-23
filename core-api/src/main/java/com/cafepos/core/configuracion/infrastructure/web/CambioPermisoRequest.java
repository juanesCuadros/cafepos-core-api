package com.cafepos.core.configuracion.infrastructure.web;

import jakarta.validation.constraints.NotNull;

public record CambioPermisoRequest(
        @NotNull(message = "permiso_id es obligatorio")
        Integer permisoId,

        @NotNull(message = "activo es obligatorio")
        Boolean activo) {
}

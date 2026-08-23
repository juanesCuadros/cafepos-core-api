package com.cafepos.core.configuracion.infrastructure.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PermisosActualizarRequest(
        @NotNull(message = "permisos es obligatorio")
        @Valid
        List<CambioPermisoRequest> permisos) {
}

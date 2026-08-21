package com.cafepos.admin.planes.infrastructure.web;

import jakarta.validation.constraints.Pattern;

public record CambiarEstadoPlanRequest(
        @Pattern(regexp = "activo|inactivo", message = "El estado debe ser 'activo' o 'inactivo'")
        String estado) {
}

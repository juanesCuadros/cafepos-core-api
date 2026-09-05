package com.cafepos.admin.negocios.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CambiarEstadoTenantRequest(
        @NotBlank(message = "El motivo es obligatorio")
        String motivo,
        LocalDate proximaFacturacion
) {
}

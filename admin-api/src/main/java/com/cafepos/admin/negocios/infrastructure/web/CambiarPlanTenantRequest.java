package com.cafepos.admin.negocios.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CambiarPlanTenantRequest(
        @NotNull(message = "El nuevo plan es obligatorio")
        Integer nuevoPlanId,
        @NotBlank(message = "El motivo es obligatorio")
        String motivo
) {
}

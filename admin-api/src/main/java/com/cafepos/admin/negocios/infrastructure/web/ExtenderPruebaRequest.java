package com.cafepos.admin.negocios.infrastructure.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ExtenderPruebaRequest(
        @Min(value = 1, message = "Debe extender al menos 1 día")
        int diasAdicionales,
        @NotBlank(message = "El motivo es obligatorio")
        String motivo
) {
}

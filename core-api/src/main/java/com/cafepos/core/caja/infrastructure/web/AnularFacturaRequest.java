package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record AnularFacturaRequest(@NotBlank(message = "motivo es obligatorio") String motivo) {
}

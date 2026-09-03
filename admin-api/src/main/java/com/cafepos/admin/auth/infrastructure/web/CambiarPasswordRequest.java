package com.cafepos.admin.auth.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequest(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String passwordActual,
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 12, message = "La nueva contraseña debe tener al menos 12 caracteres")
        String passwordNuevo
) {
}

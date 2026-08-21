package com.cafepos.core.shared.seguridad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarPasswordInicialRequest(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String passwordActual,

        @NotBlank(message = "La contraseña nueva es obligatoria")
        @Size(min = 12, message = "La contraseña nueva debe tener al menos 12 caracteres")
        String passwordNueva) {
}

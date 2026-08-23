package com.cafepos.core.restaurante.infrastructure.web;

import jakarta.validation.constraints.NotNull;

/** Unico campo editable — activo no es nullable de negocio (siempre true o false, nunca "sin valor"). */
public record MenuDigitalActualizarRequest(
        @NotNull(message = "activo es obligatorio")
        Boolean activo) {
}

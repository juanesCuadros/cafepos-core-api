package com.cafepos.core.configuracion.infrastructure.web;

import jakarta.validation.constraints.NotNull;

public record TiempoSesionActualizarRequest(
        @NotNull(message = "minutos_inactividad es obligatorio")
        Integer minutosInactividad) {
}

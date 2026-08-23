package com.cafepos.core.operacion.infrastructure.web;

import jakarta.validation.constraints.NotNull;

public record MoverMesaRequest(
        @NotNull(message = "mesa_destino_id es obligatorio")
        Integer mesaDestinoId) {
}

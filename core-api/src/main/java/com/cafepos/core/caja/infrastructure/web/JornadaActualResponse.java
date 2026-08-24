package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.JornadaActualVista;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Optional;

/** jornada se omite del JSON cuando no hay caja abierta (jornada_abierta=false) — ver contrato api_03_caja.md. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JornadaActualResponse(boolean jornadaAbierta, JornadaActualDetalleResponse jornada) {

    public static JornadaActualResponse de(Optional<JornadaActualVista> vista) {
        return vista.map(v -> new JornadaActualResponse(true, JornadaActualDetalleResponse.de(v)))
                .orElseGet(() -> new JornadaActualResponse(false, null));
    }
}

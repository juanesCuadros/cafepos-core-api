package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.domain.Turno;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Optional;

/** turno se omite del JSON cuando no hay turno activo (turno_activo=false) — ver contrato api_02_operacion.md. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TurnoActualResponse(boolean turnoActivo, TurnoRefResponse turno) {

    public static TurnoActualResponse de(Optional<Turno> turno) {
        return turno.map(t -> new TurnoActualResponse(true, TurnoRefResponse.de(t)))
                .orElseGet(() -> new TurnoActualResponse(false, null));
    }
}

package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.TurnoResumen;

import java.util.List;

public record TurnosResponse(List<TurnoListItemResponse> turnos) {

    public static TurnosResponse de(List<TurnoResumen> lista) {
        return new TurnosResponse(lista.stream().map(TurnoListItemResponse::de).toList());
    }
}

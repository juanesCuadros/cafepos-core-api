package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.EmpleadoResumen;

import java.util.List;

public record EmpleadosResponse(List<EmpleadoListItemResponse> empleados) {

    public static EmpleadosResponse de(List<EmpleadoResumen> lista) {
        return new EmpleadosResponse(lista.stream().map(EmpleadoListItemResponse::de).toList());
    }
}

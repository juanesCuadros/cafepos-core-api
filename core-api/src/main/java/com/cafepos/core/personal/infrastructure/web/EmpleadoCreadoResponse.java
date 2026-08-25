package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.Empleado;

public record EmpleadoCreadoResponse(Integer id, String codigo, String nombre) {

    public static EmpleadoCreadoResponse de(Empleado empleado) {
        return new EmpleadoCreadoResponse(empleado.getId(), empleado.getCodigo(), empleado.getNombre());
    }
}

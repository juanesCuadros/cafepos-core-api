package com.cafepos.core.personal.infrastructure.web;

public record EmpleadoEliminadoResponse(String mensaje) {

    public static final EmpleadoEliminadoResponse INSTANCIA = new EmpleadoEliminadoResponse("Empleado eliminado");
}

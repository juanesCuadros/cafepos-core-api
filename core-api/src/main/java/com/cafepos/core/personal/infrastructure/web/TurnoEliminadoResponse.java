package com.cafepos.core.personal.infrastructure.web;

public record TurnoEliminadoResponse(String mensaje) {

    public static final TurnoEliminadoResponse INSTANCIA = new TurnoEliminadoResponse("Turno eliminado");
}

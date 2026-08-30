package com.cafepos.core.personal.domain;

public class TurnoNoEncontradoException extends RuntimeException {

    public TurnoNoEncontradoException() {
        super("El turno no existe");
    }
}

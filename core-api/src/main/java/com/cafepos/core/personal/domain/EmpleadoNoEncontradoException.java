package com.cafepos.core.personal.domain;

public class EmpleadoNoEncontradoException extends RuntimeException {

    public EmpleadoNoEncontradoException() {
        super("El empleado no existe");
    }
}

package com.cafepos.core.configuracion.domain;

public class RolNoEncontradoException extends RuntimeException {

    public RolNoEncontradoException() {
        super("Rol no encontrado");
    }
}

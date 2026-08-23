package com.cafepos.core.configuracion.domain;

public class PinNoPermitidoException extends RuntimeException {

    public PinNoPermitidoException() {
        super("El PIN solo aplica a roles Admin o Jefe");
    }
}

package com.cafepos.core.configuracion.domain;

public class ImpresoraNoEncontradaException extends RuntimeException {

    public ImpresoraNoEncontradaException() {
        super("Impresora no encontrada");
    }
}

package com.cafepos.core.shared.excepciones;

public class ArchivoInvalidoException extends RuntimeException {
    public ArchivoInvalidoException(String message) {
        super(message);
    }
}

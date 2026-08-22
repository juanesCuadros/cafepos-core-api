package com.cafepos.core.productosmenu.domain;

/** vigencia_inicio posterior a vigencia_fin. */
public class VigenciaInvalidaException extends RuntimeException {

    public VigenciaInvalidaException() {
        super("vigencia_inicio debe ser anterior o igual a vigencia_fin");
    }
}

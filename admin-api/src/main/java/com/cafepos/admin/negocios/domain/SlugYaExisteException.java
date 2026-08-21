package com.cafepos.admin.negocios.domain;

public class SlugYaExisteException extends RuntimeException {

    public SlugYaExisteException() {
        super("Ya existe un negocio con ese identificador");
    }
}

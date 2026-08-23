package com.cafepos.core.configuracion.domain;

public class UsuarioCorreoDuplicadoException extends RuntimeException {

    public UsuarioCorreoDuplicadoException() {
        super("Ya existe un usuario con este correo");
    }
}

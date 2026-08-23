package com.cafepos.core.configuracion.domain;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException() {
        super("Usuario no encontrado");
    }
}

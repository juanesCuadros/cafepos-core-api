package com.cafepos.core.caja.domain;

/** cliente_id no existe para este tenant. */
public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException() {
        super("Cliente no encontrado");
    }
}

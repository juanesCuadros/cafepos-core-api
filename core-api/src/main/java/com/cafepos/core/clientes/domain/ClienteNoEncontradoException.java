package com.cafepos.core.clientes.domain;

/** El id no existe, o no pertenece al tenant actual (Row-Level Security lo oculta igual que si no existiera). */
public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException() {
        super("Cliente no encontrado");
    }
}

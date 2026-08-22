package com.cafepos.core.productosmenu.domain;

/** El id no existe, o no pertenece al tenant actual (Row-Level Security lo oculta igual que si no existiera). */
public class ComboNoEncontradoException extends RuntimeException {

    public ComboNoEncontradoException() {
        super("Combo no encontrado");
    }
}

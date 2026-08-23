package com.cafepos.core.restaurante.domain;

/** El id no existe, o no pertenece al tenant actual (Row-Level Security lo oculta igual que si no existiera). */
public class ZonaNoEncontradaException extends RuntimeException {

    public ZonaNoEncontradaException() {
        super("Zona no encontrada");
    }
}

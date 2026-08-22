package com.cafepos.core.productosmenu.domain;

/** El id no existe, o no pertenece al tenant actual (Row-Level Security lo oculta igual que si no existiera). */
public class PromocionNoEncontradaException extends RuntimeException {

    public PromocionNoEncontradaException() {
        super("Promoción no encontrada");
    }
}

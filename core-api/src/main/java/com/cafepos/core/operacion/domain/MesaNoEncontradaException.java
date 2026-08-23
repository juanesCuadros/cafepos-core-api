package com.cafepos.core.operacion.domain;

/** La mesa no existe, o no pertenece al tenant actual (restaurante.ZonaService via NamedInterface). */
public class MesaNoEncontradaException extends RuntimeException {

    public MesaNoEncontradaException() {
        super("Mesa no encontrada");
    }
}

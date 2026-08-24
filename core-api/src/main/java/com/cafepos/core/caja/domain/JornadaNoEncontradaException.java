package com.cafepos.core.caja.domain;

/** GET /caja/jornadas/{id} — no existe, o no pertenece al tenant actual. */
public class JornadaNoEncontradaException extends RuntimeException {

    public JornadaNoEncontradaException() {
        super("Jornada no encontrada");
    }
}

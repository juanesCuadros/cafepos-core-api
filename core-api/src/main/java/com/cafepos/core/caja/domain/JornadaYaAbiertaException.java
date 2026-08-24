package com.cafepos.core.caja.domain;

/** RN-011 — ya existe una jornada 'abierta' para este tenant (indice unico parcial). */
public class JornadaYaAbiertaException extends RuntimeException {

    public JornadaYaAbiertaException() {
        super("Ya existe una caja abierta para este establecimiento");
    }
}

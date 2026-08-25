package com.cafepos.core.personal.domain;

/** 400 — hora_fin debe ser posterior a hora_inicio. */
public class HoraFinAntesDeInicioException extends RuntimeException {

    public HoraFinAntesDeInicioException() {
        super("hora_fin debe ser posterior a hora_inicio");
    }
}

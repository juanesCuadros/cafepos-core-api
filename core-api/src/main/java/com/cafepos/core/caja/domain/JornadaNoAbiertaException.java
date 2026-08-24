package com.cafepos.core.caja.domain;

/** RN-010 — no hay jornada abierta para ingreso/egreso/cerrar/cobrar. Mensaje exacto del contrato (api_03_caja.md). */
public class JornadaNoAbiertaException extends RuntimeException {

    public JornadaNoAbiertaException() {
        super("No hay una caja abierta. Debes abrir jornada antes de cobrar.");
    }
}

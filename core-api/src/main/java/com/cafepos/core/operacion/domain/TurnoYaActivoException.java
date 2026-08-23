package com.cafepos.core.operacion.domain;

/** El usuario ya tiene un turno abierto (hora_fin IS NULL) — ver TurnoService.iniciar. */
public class TurnoYaActivoException extends RuntimeException {

    public TurnoYaActivoException() {
        super("Ya tienes un turno activo");
    }
}

package com.cafepos.core.gastos.domain;

/** El id no existe, o no pertenece al tenant actual (Row-Level Security lo oculta igual que si no existiera). */
public class GastoNoEncontradoException extends RuntimeException {

    public GastoNoEncontradoException() {
        super("Gasto no encontrado");
    }
}

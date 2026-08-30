package com.cafepos.core.gastos.domain;

/** El id no existe, o no pertenece al tenant actual (Row-Level Security lo oculta igual que si no existiera). */
public class CategoriaGastoNoEncontradaException extends RuntimeException {

    public CategoriaGastoNoEncontradaException() {
        super("Categoria de gasto no encontrada");
    }
}

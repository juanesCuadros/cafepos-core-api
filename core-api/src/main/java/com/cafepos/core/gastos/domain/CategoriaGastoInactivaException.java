package com.cafepos.core.gastos.domain;

/** Mensaje textual identico al contrato (api_09_gastos.md) — con tildes a proposito, es texto de error para el usuario final. */
public class CategoriaGastoInactivaException extends RuntimeException {

    public CategoriaGastoInactivaException() {
        super("Esta categoría de gasto está inactiva");
    }
}

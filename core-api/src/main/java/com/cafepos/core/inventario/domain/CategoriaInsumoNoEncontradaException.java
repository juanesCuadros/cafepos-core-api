package com.cafepos.core.inventario.domain;

/** El id no existe, o no pertenece al tenant actual (Row-Level Security lo oculta igual que si no existiera). */
public class CategoriaInsumoNoEncontradaException extends RuntimeException {

    public CategoriaInsumoNoEncontradaException() {
        super("Categoría de insumo no encontrada");
    }
}

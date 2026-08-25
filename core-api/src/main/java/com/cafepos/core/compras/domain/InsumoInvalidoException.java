package com.cafepos.core.compras.domain;

/** insumo_id de una linea de detalle no existe — 400, no 404, porque viaja anidado dentro del body, no es un recurso de la URL. */
public class InsumoInvalidoException extends RuntimeException {

    public InsumoInvalidoException() {
        super("Uno de los insumos del detalle no existe");
    }
}

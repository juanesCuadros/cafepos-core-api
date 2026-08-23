package com.cafepos.core.inventario.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonInclude;

/** "estado" solo viaja en la respuesta cuando el insumo termino inactivo, no cuando se borro fisicamente. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InsumoEliminadoResponse(String mensaje, String estado) {

    public static final InsumoEliminadoResponse ELIMINADO = new InsumoEliminadoResponse("Insumo eliminado", null);

    public static InsumoEliminadoResponse marcadoInactivo() {
        return new InsumoEliminadoResponse(
                "Este insumo tiene movimientos registrados, se marcó como inactivo en vez de eliminarse", "inactivo");
    }
}

package com.cafepos.core.productosmenu.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonInclude;

/** "estado" solo viaja en la respuesta cuando el producto termino inactivo, no cuando se borro fisicamente. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductoEliminadoResponse(String mensaje, String estado) {

    public static final ProductoEliminadoResponse ELIMINADO = new ProductoEliminadoResponse("Producto eliminado", null);

    public static ProductoEliminadoResponse marcadoInactivo() {
        return new ProductoEliminadoResponse(
                "Este producto tiene ventas registradas, se marcó como inactivo en vez de eliminarse", "inactivo");
    }
}

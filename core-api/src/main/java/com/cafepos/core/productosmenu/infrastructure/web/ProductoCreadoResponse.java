package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.Producto;

import java.time.OffsetDateTime;

/** Respuesta de POST/PATCH /productos — misma forma minima que especifica el contrato para el POST. */
public record ProductoCreadoResponse(Integer id, String codigo, String nombre, OffsetDateTime createdAt) {

    public static ProductoCreadoResponse de(Producto producto) {
        return new ProductoCreadoResponse(producto.getId(), producto.getCodigo(), producto.getNombre(),
                producto.getCreatedAt());
    }
}

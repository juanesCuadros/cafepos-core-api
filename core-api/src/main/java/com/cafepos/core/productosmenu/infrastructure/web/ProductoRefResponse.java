package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.ProductoRef;

/** Forma anidada de "productos" dentro de las respuestas de promocion - solo id+nombre. */
public record ProductoRefResponse(Integer id, String nombre) {

    public static ProductoRefResponse de(ProductoRef ref) {
        return new ProductoRefResponse(ref.id(), ref.nombre());
    }
}

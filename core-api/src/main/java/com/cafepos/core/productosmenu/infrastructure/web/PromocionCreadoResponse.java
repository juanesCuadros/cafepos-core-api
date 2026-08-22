package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.ProductoRef;
import com.cafepos.core.productosmenu.domain.Promocion;

import java.util.List;

/** Respuesta de POST/PATCH /promociones — misma forma minima que especifica el contrato para el POST. */
public record PromocionCreadoResponse(Integer id, String nombre, List<ProductoRefResponse> productos) {

    public static PromocionCreadoResponse de(Promocion promocion, List<ProductoRef> productos) {
        return new PromocionCreadoResponse(promocion.getId(), promocion.getNombre(),
                productos.stream().map(ProductoRefResponse::de).toList());
    }
}

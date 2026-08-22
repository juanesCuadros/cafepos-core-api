package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.ComboGrupoDetalle;

import java.util.List;

/** Forma anidada de un grupo dentro de la respuesta de combo — id+nombre+productos (cada producto id+nombre). */
public record ComboGrupoResponse(Integer id, String nombre, List<ProductoRefResponse> productos) {

    public static ComboGrupoResponse de(ComboGrupoDetalle detalle) {
        return new ComboGrupoResponse(detalle.id(), detalle.nombre(),
                detalle.productos().stream().map(ProductoRefResponse::de).toList());
    }
}

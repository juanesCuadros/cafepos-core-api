package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.ProveedorResumen;

public record ProveedorListItemResponse(Integer id, String codigo, String nombre, String nit, String contacto,
                                         String telefono, String estado) {

    public static ProveedorListItemResponse de(ProveedorResumen r) {
        return new ProveedorListItemResponse(r.id(), r.codigo(), r.nombre(), r.nit(), r.contacto(), r.telefono(),
                r.estado());
    }
}

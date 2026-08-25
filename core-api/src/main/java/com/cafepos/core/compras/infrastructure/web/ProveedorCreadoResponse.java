package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.Proveedor;

public record ProveedorCreadoResponse(Integer id, String codigo, String nombre) {

    public static ProveedorCreadoResponse de(Proveedor proveedor) {
        return new ProveedorCreadoResponse(proveedor.getId(), proveedor.getCodigo(), proveedor.getNombre());
    }
}

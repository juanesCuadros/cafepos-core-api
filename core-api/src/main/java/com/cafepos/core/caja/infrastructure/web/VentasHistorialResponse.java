package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.VentaResumenVista;

import java.util.List;

public record VentasHistorialResponse(List<VentaHistorialItemResponse> ventas) {

    public static VentasHistorialResponse de(List<VentaResumenVista> vistas) {
        return new VentasHistorialResponse(vistas.stream().map(VentaHistorialItemResponse::de).toList());
    }
}

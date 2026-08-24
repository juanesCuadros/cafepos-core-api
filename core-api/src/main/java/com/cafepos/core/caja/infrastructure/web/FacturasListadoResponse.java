package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.FacturaListadoItem;

import java.util.List;

public record FacturasListadoResponse(List<FacturaListadoItemResponse> facturas) {

    public static FacturasListadoResponse de(List<FacturaListadoItem> items) {
        return new FacturasListadoResponse(items.stream().map(FacturaListadoItemResponse::de).toList());
    }
}

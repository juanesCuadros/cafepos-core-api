package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.DevolucionListadoItem;

import java.util.List;

public record DevolucionesListadoResponse(List<DevolucionListadoItemResponse> devoluciones) {

    public static DevolucionesListadoResponse de(List<DevolucionListadoItem> items) {
        return new DevolucionesListadoResponse(items.stream().map(DevolucionListadoItemResponse::de).toList());
    }
}

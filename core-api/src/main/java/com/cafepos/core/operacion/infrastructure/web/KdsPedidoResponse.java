package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.KdsPedidoVista;

import java.util.List;

public record KdsPedidoResponse(Integer pedidoId, String numeroOrden, String mesa, String tipo,
                                 List<KdsItemResponse> items) {

    public static KdsPedidoResponse de(KdsPedidoVista vista) {
        return new KdsPedidoResponse(vista.pedidoId(), vista.numeroOrden(), vista.mesa(), vista.tipo(),
                vista.items().stream().map(KdsItemResponse::de).toList());
    }
}

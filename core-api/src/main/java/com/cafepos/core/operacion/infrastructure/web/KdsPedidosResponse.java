package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.KdsPedidoVista;

import java.util.List;

public record KdsPedidosResponse(List<KdsPedidoResponse> pedidos) {

    public static KdsPedidosResponse de(List<KdsPedidoVista> vistas) {
        return new KdsPedidosResponse(vistas.stream().map(KdsPedidoResponse::de).toList());
    }
}

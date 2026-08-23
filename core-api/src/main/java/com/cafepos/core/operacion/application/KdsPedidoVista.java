package com.cafepos.core.operacion.application;

import java.util.List;

/** Fila de GET /operacion/kds/pedidos — ver KdsService. */
public record KdsPedidoVista(Integer pedidoId, String numeroOrden, String mesa, String tipo,
                              List<KdsItemVista> items) {
}

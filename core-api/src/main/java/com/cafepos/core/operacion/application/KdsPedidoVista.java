package com.cafepos.core.operacion.application;

import java.time.OffsetDateTime;
import java.util.List;

/** Fila de GET /operacion/kds/pedidos — ver KdsService. */
public record KdsPedidoVista(Integer pedidoId, String numeroOrden, String mesa, String tipo,
                              OffsetDateTime fechaEnviado, List<KdsItemVista> items) {
}

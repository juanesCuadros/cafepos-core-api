package com.cafepos.core.caja.domain;

import java.math.BigDecimal;
import java.util.List;

/** Puerto de persistencia de DevolucionItem — implementado en infrastructure.persistence. */
public interface DevolucionItemRepository {

    DevolucionItem guardar(DevolucionItem item);

    List<DevolucionItem> listarDeDevolucion(Integer devolucionId);

    /** Suma de todo lo ya devuelto de este pedido_item, sumando TODAS las devoluciones previas — nunca null (0 si no hay ninguna). */
    BigDecimal sumaCantidadDevueltaDeItem(Integer pedidoItemId);
}

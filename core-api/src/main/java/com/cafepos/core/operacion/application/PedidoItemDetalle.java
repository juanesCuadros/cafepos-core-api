package com.cafepos.core.operacion.application;

import java.math.BigDecimal;

/** Vista de un PedidoItem con el nombre ya resuelto (producto o combo) — ver PedidoService. */
public record PedidoItemDetalle(Integer id, Integer productoId, Integer comboId, String nombre, BigDecimal cantidad,
                                 BigDecimal precioUnitario, String observacion, String estadoPreparacion,
                                 BigDecimal subtotal) {
}

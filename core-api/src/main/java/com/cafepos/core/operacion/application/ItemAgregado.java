package com.cafepos.core.operacion.application;

import java.math.BigDecimal;

/** Resultado de PedidoService.agregarItem — ver PedidoController. */
public record ItemAgregado(PedidoItemDetalle item, BigDecimal pedidoSubtotal) {
}

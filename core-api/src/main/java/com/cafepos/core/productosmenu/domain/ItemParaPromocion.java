package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;

/**
 * Descripcion minima de un item de pedido que com.cafepos.core.operacion
 * envia para evaluar promociones aplicables — ver PromocionService.evaluarSugeridas.
 *
 * @NamedInterface propio, ver PromocionService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("itemParaPromocion")
public record ItemParaPromocion(Integer productoId, BigDecimal cantidad, BigDecimal subtotal) {
}

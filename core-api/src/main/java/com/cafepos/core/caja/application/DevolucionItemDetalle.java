package com.cafepos.core.caja.application;

import java.math.BigDecimal;

/** nombre/precioUnitario null si el pedido_item ya no se pudo resolver (pedido muy viejo) — caso defensivo. */
public record DevolucionItemDetalle(Integer pedidoItemId, String nombre, BigDecimal cantidad,
                                     BigDecimal precioUnitario, BigDecimal subtotal) {
}

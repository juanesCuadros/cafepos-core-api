package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.DevolucionItemDetalle;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record DevolucionItemDetalleResponse(Integer pedidoItemId, String nombre, BigDecimal cantidad,
                                             @Monto BigDecimal precioUnitario, @Monto BigDecimal subtotal) {

    public static DevolucionItemDetalleResponse de(DevolucionItemDetalle item) {
        return new DevolucionItemDetalleResponse(item.pedidoItemId(), item.nombre(), item.cantidad(),
                item.precioUnitario(), item.subtotal());
    }
}

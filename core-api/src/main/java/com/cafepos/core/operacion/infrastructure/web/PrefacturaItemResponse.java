package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.PedidoItemDetalle;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record PrefacturaItemResponse(String nombre, BigDecimal cantidad, @Monto BigDecimal precioUnitario,
                                      @Monto BigDecimal subtotal) {

    public static PrefacturaItemResponse de(PedidoItemDetalle item) {
        return new PrefacturaItemResponse(item.nombre(), item.cantidad(), item.precioUnitario(), item.subtotal());
    }
}

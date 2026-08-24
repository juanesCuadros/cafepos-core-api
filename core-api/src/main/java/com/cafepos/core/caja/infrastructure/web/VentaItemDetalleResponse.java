package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.operacion.domain.PedidoItemParaVenta;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record VentaItemDetalleResponse(String nombre, BigDecimal cantidad, @Monto BigDecimal precioUnitario,
                                        @Monto BigDecimal subtotal) {

    public static VentaItemDetalleResponse de(PedidoItemParaVenta item) {
        return new VentaItemDetalleResponse(item.nombre(), item.cantidad(), item.precioUnitario(),
                item.precioUnitario().multiply(item.cantidad()));
    }
}

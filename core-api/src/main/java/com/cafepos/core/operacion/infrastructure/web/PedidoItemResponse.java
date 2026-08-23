package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.PedidoItemDetalle;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record PedidoItemResponse(Integer id, Integer productoId, Integer comboId, String nombre,
                                  BigDecimal cantidad, @Monto BigDecimal precioUnitario, String observacion,
                                  String estadoPreparacion, @Monto BigDecimal subtotal) {

    public static PedidoItemResponse de(PedidoItemDetalle item) {
        return new PedidoItemResponse(item.id(), item.productoId(), item.comboId(), item.nombre(), item.cantidad(),
                item.precioUnitario(), item.observacion(), item.estadoPreparacion(), item.subtotal());
    }
}

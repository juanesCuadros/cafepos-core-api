package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.ItemAgregado;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record ItemAgregadoResponse(Integer id, Integer productoId, Integer comboId, String nombre,
                                    BigDecimal cantidad, @Monto BigDecimal precioUnitario, String observacion,
                                    String estadoPreparacion, @Monto BigDecimal subtotal,
                                    @Monto BigDecimal pedidoSubtotal) {

    public static ItemAgregadoResponse de(ItemAgregado resultado) {
        var item = resultado.item();
        return new ItemAgregadoResponse(item.id(), item.productoId(), item.comboId(), item.nombre(),
                item.cantidad(), item.precioUnitario(), item.observacion(), item.estadoPreparacion(),
                item.subtotal(), resultado.pedidoSubtotal());
    }
}

package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record ItemEliminadoResponse(String mensaje, @Monto BigDecimal pedidoSubtotal) {

    public static ItemEliminadoResponse de(BigDecimal pedidoSubtotal) {
        return new ItemEliminadoResponse("Ítem eliminado", pedidoSubtotal);
    }
}

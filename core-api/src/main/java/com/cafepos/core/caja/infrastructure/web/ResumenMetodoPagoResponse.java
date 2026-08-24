package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.ResumenMetodoPago;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record ResumenMetodoPagoResponse(String metodo, @Monto BigDecimal total) {

    public static ResumenMetodoPagoResponse de(ResumenMetodoPago resumen) {
        return new ResumenMetodoPagoResponse(resumen.metodo(), resumen.total());
    }
}

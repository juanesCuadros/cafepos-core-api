package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.VentaPagoDetalle;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record VentaPagoDetalleResponse(String metodoPago, @Monto BigDecimal monto) {

    public static VentaPagoDetalleResponse de(VentaPagoDetalle pago) {
        return new VentaPagoDetalleResponse(pago.metodoPagoNombre(), pago.monto());
    }
}

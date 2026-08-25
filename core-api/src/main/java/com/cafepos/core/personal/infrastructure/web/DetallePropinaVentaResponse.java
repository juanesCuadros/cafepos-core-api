package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.application.DetallePropinaVenta;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DetallePropinaVentaResponse(String ventaCodigo, OffsetDateTime fecha,
                                           @Monto BigDecimal propinaTotalVenta, @Monto BigDecimal montoAtribuido) {

    public static DetallePropinaVentaResponse de(DetallePropinaVenta d) {
        return new DetallePropinaVentaResponse(d.ventaCodigo(), d.fecha(), d.propinaTotalVenta(),
                d.montoAtribuido());
    }
}

package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.FacturaListadoItem;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FacturaListadoItemResponse(Integer id, String numeroFactura, OffsetDateTime fechaEmision,
                                          String cliente, @Monto BigDecimal total, String estadoDian) {

    public static FacturaListadoItemResponse de(FacturaListadoItem item) {
        return new FacturaListadoItemResponse(item.id(), item.numeroFactura(), item.fechaEmision(), item.cliente(),
                item.total(), item.estadoDian());
    }
}

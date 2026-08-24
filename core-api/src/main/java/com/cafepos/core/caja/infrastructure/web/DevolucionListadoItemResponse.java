package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.DevolucionListadoItem;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DevolucionListadoItemResponse(Integer id, String ventaCodigo, OffsetDateTime fecha, String cliente,
                                             @Monto BigDecimal montoDevuelto, String metodoReembolso,
                                             String estado) {

    public static DevolucionListadoItemResponse de(DevolucionListadoItem item) {
        return new DevolucionListadoItemResponse(item.id(), item.ventaCodigo(), item.fecha(), item.cliente(),
                item.montoDevuelto(), item.metodoReembolso(), item.estado());
    }
}

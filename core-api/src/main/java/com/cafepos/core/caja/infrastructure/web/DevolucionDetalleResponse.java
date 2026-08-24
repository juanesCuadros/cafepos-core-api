package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.DevolucionDetalleVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record DevolucionDetalleResponse(Integer id, Integer ventaId, String ventaCodigo,
                                         ClienteVentaResponse cliente, String motivo,
                                         @Monto BigDecimal montoDevuelto, String metodoReembolso, String estado,
                                         OffsetDateTime fecha, List<DevolucionItemDetalleResponse> items) {

    public static DevolucionDetalleResponse de(DevolucionDetalleVista vista) {
        var d = vista.devolucion();
        return new DevolucionDetalleResponse(d.getId(), d.getVentaId(), vista.ventaCodigo(),
                ClienteVentaResponse.de(vista.cliente()), d.getMotivo(), d.getMontoDevuelto(),
                d.getMetodoReembolso(), d.getEstado(), d.getFecha(),
                vista.items().stream().map(DevolucionItemDetalleResponse::de).toList());
    }
}

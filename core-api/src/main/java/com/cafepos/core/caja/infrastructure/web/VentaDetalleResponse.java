package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.VentaDetalleVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record VentaDetalleResponse(Integer id, String codigo, Integer pedidoId, String cajero,
                                    @Monto BigDecimal subtotal, @Monto BigDecimal descuentoTotal,
                                    @Monto BigDecimal impuestos, @Monto BigDecimal propina, @Monto BigDecimal total,
                                    String estado, ClienteVentaResponse cliente, FacturaVentaResponse factura,
                                    OffsetDateTime fechaHora, List<VentaItemDetalleResponse> items,
                                    List<VentaPagoDetalleResponse> pagos) {

    public static VentaDetalleResponse de(VentaDetalleVista vista) {
        var v = vista.venta();
        return new VentaDetalleResponse(v.getId(), v.getCodigo(), v.getPedidoId(), vista.cajeroNombre(),
                v.getSubtotal(), v.getDescuentoTotal(), v.getImpuestos(), v.getPropina(), v.getTotal(), v.getEstado(),
                ClienteVentaResponse.de(vista.cliente()), FacturaVentaResponse.de(vista.factura()), v.getFechaHora(),
                vista.items().stream().map(VentaItemDetalleResponse::de).toList(),
                vista.pagos().stream().map(VentaPagoDetalleResponse::de).toList());
    }
}

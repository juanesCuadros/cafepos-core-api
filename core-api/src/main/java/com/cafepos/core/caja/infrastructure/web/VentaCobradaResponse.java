package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.VentaResultado;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Respuesta de POST /ventas — cliente/factura viajan como null explicito (no se omiten), ver contrato api_03_caja.md. */
public record VentaCobradaResponse(Integer id, String codigo, Integer pedidoId, @Monto BigDecimal subtotal,
                                    @Monto BigDecimal descuentoTotal, @Monto BigDecimal impuestos,
                                    @Monto BigDecimal propina, @Monto BigDecimal total, String estado,
                                    ClienteVentaResponse cliente, FacturaVentaResponse factura,
                                    OffsetDateTime fechaHora) {

    public static VentaCobradaResponse de(VentaResultado resultado) {
        var v = resultado.venta();
        return new VentaCobradaResponse(v.getId(), v.getCodigo(), v.getPedidoId(), v.getSubtotal(),
                v.getDescuentoTotal(), v.getImpuestos(), v.getPropina(), v.getTotal(), v.getEstado(),
                ClienteVentaResponse.de(resultado.cliente()), FacturaVentaResponse.de(resultado.factura()),
                v.getFechaHora());
    }
}

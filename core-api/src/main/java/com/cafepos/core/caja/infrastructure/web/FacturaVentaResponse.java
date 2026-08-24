package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.FacturaResumen;

/** cufe SIEMPRE null en este prompt — sin transmision real a Factus (queda para un prompt futuro). */
public record FacturaVentaResponse(Integer id, String numeroFactura, String estadoDian, String cufe) {

    public static FacturaVentaResponse de(FacturaResumen factura) {
        return factura == null ? null
                : new FacturaVentaResponse(factura.id(), factura.numeroFactura(), factura.estadoDian(), null);
    }
}

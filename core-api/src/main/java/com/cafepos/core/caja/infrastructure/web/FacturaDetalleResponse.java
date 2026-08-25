package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.FacturaDetalleVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** cufe/qr_code null hasta que la transmision real a Factus tenga exito (ver FacturaDian). */
public record FacturaDetalleResponse(Integer id, String numeroFactura, OffsetDateTime fechaEmision, String cufe,
                                      String qrCode, String estadoDian, String motivoRechazo,
                                      @Monto BigDecimal total, ClienteFacturaResponse cliente) {

    public static FacturaDetalleResponse de(FacturaDetalleVista vista) {
        var f = vista.factura();
        return new FacturaDetalleResponse(f.getId(), f.getNumeroFactura(), f.getFechaEmision(), f.getCufe(),
                f.getQrCode(), f.getEstadoDian(), f.getMotivoRechazo(), vista.ventaTotal(),
                ClienteFacturaResponse.de(vista.cliente()));
    }
}

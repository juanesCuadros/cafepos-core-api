package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.VentaResumenVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record VentaHistorialItemResponse(Integer id, String codigo, OffsetDateTime fechaHora, String cajero,
                                          List<String> metodosPago, @Monto BigDecimal total, String estado) {

    public static VentaHistorialItemResponse de(VentaResumenVista vista) {
        var v = vista.venta();
        return new VentaHistorialItemResponse(v.getId(), v.getCodigo(), v.getFechaHora(), vista.cajeroNombre(),
                vista.metodosPago(), v.getTotal(), v.getEstado());
    }
}

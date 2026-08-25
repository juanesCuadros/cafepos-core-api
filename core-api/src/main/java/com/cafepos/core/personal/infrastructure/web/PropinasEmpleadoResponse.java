package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.application.ResumenPropinas;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record PropinasEmpleadoResponse(@Monto BigDecimal totalPropinas, java.util.List<DetallePropinaVentaResponse> detalle) {

    public static PropinasEmpleadoResponse de(ResumenPropinas r) {
        return new PropinasEmpleadoResponse(r.totalPropinas(),
                r.detalle().stream().map(DetallePropinaVentaResponse::de).toList());
    }
}

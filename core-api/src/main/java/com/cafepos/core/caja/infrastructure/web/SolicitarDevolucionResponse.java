package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.DevolucionResultado;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record SolicitarDevolucionResponse(Integer id, @Monto BigDecimal montoDevuelto, String metodoReembolso,
                                           String estado, boolean notaCreditoGenerada, Integer notaCreditoId,
                                           String detalle) {

    public static SolicitarDevolucionResponse de(DevolucionResultado resultado) {
        var d = resultado.devolucion();
        return new SolicitarDevolucionResponse(d.getId(), d.getMontoDevuelto(), d.getMetodoReembolso(), d.getEstado(),
                resultado.notaCreditoGenerada(), resultado.notaCreditoId(), resultado.detalle());
    }
}

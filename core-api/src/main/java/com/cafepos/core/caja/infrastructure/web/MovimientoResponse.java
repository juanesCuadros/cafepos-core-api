package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.CajaMovimiento;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record MovimientoResponse(Integer id, String tipo, @Monto BigDecimal monto, String motivo) {

    public static MovimientoResponse de(CajaMovimiento movimiento) {
        return new MovimientoResponse(movimiento.getId(), movimiento.getTipo(), movimiento.getMonto(),
                movimiento.getMotivo());
    }
}

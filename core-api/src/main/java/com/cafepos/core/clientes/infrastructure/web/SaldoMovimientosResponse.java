package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.SaldoMovimientosVista;

import java.math.BigDecimal;
import java.util.List;

/** movimientos vacio hasta que exista Caja/Devoluciones — correcto, no es un bug. */
public record SaldoMovimientosResponse(BigDecimal saldoActual, List<SaldoMovimientoResponse> movimientos) {

    public static SaldoMovimientosResponse de(SaldoMovimientosVista vista) {
        return new SaldoMovimientosResponse(vista.saldoActual(),
                vista.movimientos().stream().map(SaldoMovimientoResponse::de).toList());
    }
}

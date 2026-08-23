package com.cafepos.core.clientes.domain;

import java.math.BigDecimal;
import java.util.List;

/** GET /clientes/{id}/saldo-movimientos — saldoActual sale de cliente.saldoFavor, ya en sync (nada mas lo toca todavia). */
public record SaldoMovimientosVista(BigDecimal saldoActual, List<SaldoMovimientoItem> movimientos) {
}

package com.cafepos.core.clientes.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Fila de GET /clientes/{id}/saldo-movimientos — tabla cliente_saldo_movimiento, la llenan Caja/Devoluciones (no existen todavia). */
public record SaldoMovimientoItem(Integer id, String tipo, BigDecimal monto, String origenTipo, Integer origenId,
                                   OffsetDateTime fecha, String descripcion) {
}

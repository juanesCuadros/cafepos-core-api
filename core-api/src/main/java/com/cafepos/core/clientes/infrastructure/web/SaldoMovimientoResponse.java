package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.SaldoMovimientoItem;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaldoMovimientoResponse(Integer id, String tipo, BigDecimal monto, String origenTipo,
                                       Integer origenId, OffsetDateTime fecha, String descripcion) {

    public static SaldoMovimientoResponse de(SaldoMovimientoItem item) {
        return new SaldoMovimientoResponse(item.id(), item.tipo(), item.monto(), item.origenTipo(),
                item.origenId(), item.fecha(), item.descripcion());
    }
}

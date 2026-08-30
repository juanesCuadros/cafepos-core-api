package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.domain.GastoResumen;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoListItemResponse(Integer id, String codigo, LocalDate fecha, String categoria, String descripcion,
                                     @Monto BigDecimal monto, String metodoPago, String usuario) {

    public static GastoListItemResponse de(GastoResumen resumen) {
        return new GastoListItemResponse(resumen.id(), resumen.codigo(), resumen.fecha(), resumen.categoria(),
                resumen.descripcion(), resumen.monto(), resumen.metodoPago(), resumen.usuario());
    }
}

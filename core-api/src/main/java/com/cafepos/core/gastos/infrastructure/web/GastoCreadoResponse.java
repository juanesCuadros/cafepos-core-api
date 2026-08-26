package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.domain.Gasto;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record GastoCreadoResponse(Integer id, String codigo, @Monto BigDecimal monto) {

    public static GastoCreadoResponse de(Gasto gasto) {
        return new GastoCreadoResponse(gasto.getId(), gasto.getCodigo(), gasto.getMonto());
    }
}

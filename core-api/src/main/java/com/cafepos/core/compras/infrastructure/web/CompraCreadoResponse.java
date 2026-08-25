package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.Compra;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record CompraCreadoResponse(Integer id, String codigo, String estado, @Monto BigDecimal total) {

    public static CompraCreadoResponse de(Compra compra) {
        return new CompraCreadoResponse(compra.getId(), compra.getCodigo(), compra.getEstado(), compra.getTotal());
    }
}

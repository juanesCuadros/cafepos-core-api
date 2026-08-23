package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.AjusteResultado;

import java.math.BigDecimal;

public record AjusteResponse(Integer movimientoId, Integer insumoId, BigDecimal stockAnterior,
                              BigDecimal stockNuevo) {

    public static AjusteResponse de(AjusteResultado resultado) {
        return new AjusteResponse(resultado.movimientoId(), resultado.insumoId(), resultado.stockAnterior(),
                resultado.stockNuevo());
    }
}

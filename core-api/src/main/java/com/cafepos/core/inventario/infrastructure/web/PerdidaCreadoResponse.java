package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.PerdidaResultado;

import java.math.BigDecimal;

public record PerdidaCreadoResponse(Integer id, BigDecimal costoCalculado, BigDecimal stockAnterior,
                                     BigDecimal stockNuevo) {

    public static PerdidaCreadoResponse de(PerdidaResultado resultado) {
        return new PerdidaCreadoResponse(resultado.id(), resultado.costoCalculado(), resultado.stockAnterior(),
                resultado.stockNuevo());
    }
}

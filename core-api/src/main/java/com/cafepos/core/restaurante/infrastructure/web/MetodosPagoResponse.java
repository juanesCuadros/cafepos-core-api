package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MetodoPagoResumen;

import java.util.List;

public record MetodosPagoResponse(List<MetodoPagoListItemResponse> metodosPago) {

    public static MetodosPagoResponse de(List<MetodoPagoResumen> resumenes) {
        return new MetodosPagoResponse(resumenes.stream().map(MetodoPagoListItemResponse::de).toList());
    }
}

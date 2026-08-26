package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.domain.GastoResumen;

import java.util.List;

public record GastosResponse(List<GastoListItemResponse> gastos) {

    public static GastosResponse de(List<GastoResumen> gastos) {
        return new GastosResponse(gastos.stream().map(GastoListItemResponse::de).toList());
    }
}

package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.PerdidaResumen;

import java.util.List;

public record PerdidasResponse(List<PerdidaListItemResponse> perdidas) {

    public static PerdidasResponse de(List<PerdidaResumen> resumenes) {
        return new PerdidasResponse(resumenes.stream().map(PerdidaListItemResponse::de).toList());
    }
}

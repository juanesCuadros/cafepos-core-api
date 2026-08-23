package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MesaResumen;

import java.util.List;

public record MesasResponse(List<MesaListItemResponse> mesas) {

    public static MesasResponse de(List<MesaResumen> resumenes) {
        return new MesasResponse(resumenes.stream().map(MesaListItemResponse::de).toList());
    }
}

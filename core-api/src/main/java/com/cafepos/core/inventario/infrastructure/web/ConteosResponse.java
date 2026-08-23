package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.ConteoResumen;

import java.util.List;

public record ConteosResponse(List<ConteoListItemResponse> conteos) {

    public static ConteosResponse de(List<ConteoResumen> resumenes) {
        return new ConteosResponse(resumenes.stream().map(ConteoListItemResponse::de).toList());
    }
}

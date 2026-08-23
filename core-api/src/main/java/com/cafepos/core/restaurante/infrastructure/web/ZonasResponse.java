package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.ZonaResumen;

import java.util.List;

public record ZonasResponse(List<ZonaListItemResponse> zonas) {

    public static ZonasResponse de(List<ZonaResumen> resumenes) {
        return new ZonasResponse(resumenes.stream().map(ZonaListItemResponse::de).toList());
    }
}

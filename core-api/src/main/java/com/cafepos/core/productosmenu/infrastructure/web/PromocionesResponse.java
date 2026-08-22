package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.PromocionResumen;

import java.util.List;

public record PromocionesResponse(List<PromocionListItemResponse> promociones) {

    public static PromocionesResponse de(List<PromocionResumen> resumenes) {
        return new PromocionesResponse(resumenes.stream().map(PromocionListItemResponse::de).toList());
    }
}

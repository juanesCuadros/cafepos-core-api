package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.ComboResumen;

import java.util.List;

public record CombosResponse(List<ComboListItemResponse> combos) {

    public static CombosResponse de(List<ComboResumen> resumenes) {
        return new CombosResponse(resumenes.stream().map(ComboListItemResponse::de).toList());
    }
}

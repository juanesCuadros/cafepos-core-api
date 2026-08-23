package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.InsumoResumen;

import java.util.List;

public record InsumosResponse(List<InsumoListItemResponse> insumos) {

    public static InsumosResponse de(List<InsumoResumen> resumenes) {
        return new InsumosResponse(resumenes.stream().map(InsumoListItemResponse::de).toList());
    }
}

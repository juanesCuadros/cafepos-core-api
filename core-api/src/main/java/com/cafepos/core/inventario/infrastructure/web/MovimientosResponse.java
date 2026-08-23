package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.MovimientoInventarioResumen;

import java.util.List;

public record MovimientosResponse(List<MovimientoListItemResponse> movimientos) {

    public static MovimientosResponse de(List<MovimientoInventarioResumen> resumenes) {
        return new MovimientosResponse(resumenes.stream().map(MovimientoListItemResponse::de).toList());
    }
}

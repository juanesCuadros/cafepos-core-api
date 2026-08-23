package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.LoteVencimiento;

import java.util.List;

public record VencimientosResponse(List<LoteVencimientoResponse> lotes) {

    public static VencimientosResponse de(List<LoteVencimiento> lotes) {
        return new VencimientosResponse(lotes.stream().map(LoteVencimientoResponse::de).toList());
    }
}

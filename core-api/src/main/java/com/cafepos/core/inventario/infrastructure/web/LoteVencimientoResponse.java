package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.LoteVencimiento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteVencimientoResponse(Integer loteId, InsumoRefResponse insumo, BigDecimal stockActualInsumo,
                                       String numeroLote, LocalDate fechaVencimiento, long diasRestantes,
                                       String estado) {

    public static LoteVencimientoResponse de(LoteVencimiento lote) {
        return new LoteVencimientoResponse(lote.loteId(),
                new InsumoRefResponse(lote.insumoId(), lote.insumoCodigo(), lote.insumoNombre()),
                lote.stockActualInsumo(), lote.numeroLote(), lote.fechaVencimiento(), lote.diasRestantes(),
                lote.estado());
    }

    /** Forma anidada de "insumo" dentro de GET /lotes-insumo/vencimientos — id+codigo+nombre. */
    public record InsumoRefResponse(Integer id, String codigo, String nombre) {
    }
}

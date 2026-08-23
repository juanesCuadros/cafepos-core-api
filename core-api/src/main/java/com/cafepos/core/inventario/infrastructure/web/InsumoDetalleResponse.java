package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.Insumo;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** GET /insumos/{id} — TODOS los campos de la tabla, mas valorTotal/estadoStock calculados (ver Insumo). */
public record InsumoDetalleResponse(
        Integer id,
        String codigo,
        String nombre,
        Integer categoriaInsumoId,
        String unidadMedida,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        BigDecimal stockMaximo,
        @Monto BigDecimal costoActual,
        @Monto BigDecimal valorTotal,
        String estadoStock,
        LocalDate fechaVencimRef,
        String estado,
        OffsetDateTime fechaRegistro,
        OffsetDateTime updatedAt) {

    public static InsumoDetalleResponse de(Insumo i) {
        return new InsumoDetalleResponse(i.getId(), i.getCodigo(), i.getNombre(), i.getCategoriaInsumoId(),
                i.getUnidadMedida(), i.getStockActual(), i.getStockMinimo(), i.getStockMaximo(), i.getCostoActual(),
                i.calcularValorTotal(), i.calcularEstadoStock(), i.getFechaVencimRef(), i.getEstado(),
                i.getFechaRegistro(), i.getUpdatedAt());
    }
}

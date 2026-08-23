package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.MovimientoInventarioResumen;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimientoListItemResponse(Integer id, OffsetDateTime fechaHora, String insumo, String tipo,
                                          BigDecimal cantidad, String unidadMedida, String usuario,
                                          String motivoOrigen, String referenciaTipo, Integer referenciaId) {

    public static MovimientoListItemResponse de(MovimientoInventarioResumen resumen) {
        return new MovimientoListItemResponse(resumen.id(), resumen.fechaHora(), resumen.insumoNombre(),
                resumen.tipo(), resumen.cantidad(), resumen.unidadMedida(), resumen.usuarioNombre(),
                resumen.motivoOrigen(), resumen.referenciaTipo(), resumen.referenciaId());
    }
}

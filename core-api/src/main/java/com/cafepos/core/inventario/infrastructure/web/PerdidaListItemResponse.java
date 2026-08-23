package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.PerdidaResumen;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PerdidaListItemResponse(Integer id, LocalDate fecha, String insumo, BigDecimal cantidad,
                                       String motivo, @Monto BigDecimal costoCalculado, String usuario) {

    public static PerdidaListItemResponse de(PerdidaResumen resumen) {
        return new PerdidaListItemResponse(resumen.id(), resumen.fecha(), resumen.insumoNombre(),
                resumen.cantidad(), resumen.motivo(), resumen.costoCalculado(), resumen.usuarioNombre());
    }
}

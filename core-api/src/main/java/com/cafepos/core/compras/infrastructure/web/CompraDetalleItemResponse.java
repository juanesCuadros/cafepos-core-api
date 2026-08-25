package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.CompraDetalleItemVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompraDetalleItemResponse(Integer id, Integer insumoId, String insumoCodigo, String insumoNombre,
                                         String unidadMedida, BigDecimal cantidad, @Monto BigDecimal costoUnitario,
                                         String numeroLote, LocalDate fechaVencimiento, @Monto BigDecimal subtotal) {

    public static CompraDetalleItemResponse de(CompraDetalleItemVista v) {
        return new CompraDetalleItemResponse(v.id(), v.insumoId(), v.insumoCodigo(), v.insumoNombre(),
                v.unidadMedida(), v.cantidad(), v.costoUnitario(), v.numeroLote(), v.fechaVencimiento(),
                v.subtotal());
    }
}

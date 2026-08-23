package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.ConteoDetalleItem;

import java.math.BigDecimal;

public record ConteoDetalleItemResponse(String insumo, BigDecimal stockSistema, BigDecimal stockFisico,
                                         BigDecimal diferencia) {

    public static ConteoDetalleItemResponse de(ConteoDetalleItem item) {
        return new ConteoDetalleItemResponse(item.insumoNombre(), item.stockSistema(), item.stockFisico(),
                item.diferencia());
    }
}

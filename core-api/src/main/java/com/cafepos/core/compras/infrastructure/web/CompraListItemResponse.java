package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.CompraListadoItem;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompraListItemResponse(Integer id, String codigo, LocalDate fecha, Integer proveedorId,
                                      String proveedorNombre, String formaPago, String estado,
                                      @Monto BigDecimal total) {

    public static CompraListItemResponse de(CompraListadoItem item) {
        return new CompraListItemResponse(item.id(), item.codigo(), item.fecha(), item.proveedorId(),
                item.proveedorNombre(), item.formaPago(), item.estado(), item.total());
    }
}

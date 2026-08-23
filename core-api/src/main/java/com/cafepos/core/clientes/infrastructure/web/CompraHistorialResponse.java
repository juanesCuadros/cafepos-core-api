package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.CompraHistorial;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CompraHistorialResponse(Integer ventaId, OffsetDateTime fechaHora, @Monto BigDecimal total,
                                       String facturaNumero, String estado) {

    public static CompraHistorialResponse de(CompraHistorial compra) {
        return new CompraHistorialResponse(compra.ventaId(), compra.fechaHora(), compra.total(),
                compra.facturaNumero(), compra.estado());
    }
}

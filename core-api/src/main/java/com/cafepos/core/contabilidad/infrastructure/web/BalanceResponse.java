package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.BalanceGeneral;

import java.util.List;

public record BalanceResponse(ResumenBalanceResponse resumen,
                               List<DesgloseMetodoPagoResponse> desgloseIngresosPorMetodoPago,
                               List<DesgloseProveedorResponse> desgloseComprasPorProveedor,
                               List<DesgloseCategoriaResponse> desgloseGastosPorCategoria) {

    public static BalanceResponse de(BalanceGeneral balance) {
        return new BalanceResponse(ResumenBalanceResponse.de(balance),
                balance.desgloseIngresosPorMetodoPago().stream().map(DesgloseMetodoPagoResponse::de).toList(),
                balance.desgloseComprasPorProveedor().stream().map(DesgloseProveedorResponse::de).toList(),
                balance.desgloseGastosPorCategoria().stream().map(DesgloseCategoriaResponse::de).toList());
    }
}

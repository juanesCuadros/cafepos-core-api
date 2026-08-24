package com.cafepos.core.caja.infrastructure.web;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CobrarRequest(
        @NotNull(message = "pedido_id es obligatorio") Integer pedidoId,
        Integer clienteId,
        BigDecimal propina,
        BigDecimal descuentoTotal,
        List<PromocionAplicadaRequest> promocionesAplicadas,
        List<PagoRequest> pagos) {
}

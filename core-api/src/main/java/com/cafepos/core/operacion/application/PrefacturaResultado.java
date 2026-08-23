package com.cafepos.core.operacion.application;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Resultado de PedidoService.prefactura — ver PedidoController. */
public record PrefacturaResultado(String numeroOrden, String mesaNumero, List<PedidoItemDetalle> items,
                                   BigDecimal subtotal, OffsetDateTime generadoEn) {
}

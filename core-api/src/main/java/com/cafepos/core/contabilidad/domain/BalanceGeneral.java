package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.util.List;

public record BalanceGeneral(BigDecimal totalIngresos, BigDecimal totalCompras, BigDecimal totalGastosOperativos,
                              BigDecimal totalEgresosCaja, BigDecimal utilidadBruta,
                              List<ItemDesglose> desgloseIngresosPorMetodoPago,
                              List<ItemDesglose> desgloseComprasPorProveedor,
                              List<ItemDesglose> desgloseGastosPorCategoria) {
}

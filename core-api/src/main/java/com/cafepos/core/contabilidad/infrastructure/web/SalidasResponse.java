package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record SalidasResponse(@Monto BigDecimal comprasPagadas, @Monto BigDecimal gastosOperativos,
                               @Monto BigDecimal egresosCaja) {
}

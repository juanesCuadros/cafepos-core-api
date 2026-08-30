package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record EntradasResponse(@Monto BigDecimal ventasEfectivo, @Monto BigDecimal ventasOtrosMetodos,
                                @Monto BigDecimal ingresosCaja) {
}

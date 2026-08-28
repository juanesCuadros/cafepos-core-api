package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.BalanceGeneral;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record ResumenBalanceResponse(@Monto BigDecimal totalIngresos, @Monto BigDecimal totalCompras,
                                      @Monto BigDecimal totalGastosOperativos, @Monto BigDecimal totalEgresosCaja,
                                      @Monto BigDecimal utilidadBruta) {

    public static ResumenBalanceResponse de(BalanceGeneral balance) {
        return new ResumenBalanceResponse(balance.totalIngresos(), balance.totalCompras(),
                balance.totalGastosOperativos(), balance.totalEgresosCaja(), balance.utilidadBruta());
    }
}

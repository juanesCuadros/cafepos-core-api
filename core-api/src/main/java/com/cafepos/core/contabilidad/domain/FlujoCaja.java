package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.util.List;

public record FlujoCaja(BigDecimal saldoInicial, BigDecimal ventasEfectivo, BigDecimal ventasOtrosMetodos,
                         BigDecimal ingresosCaja, BigDecimal comprasPagadas, BigDecimal gastosOperativos,
                         BigDecimal egresosCaja, BigDecimal saldoFinal, BigDecimal diferencia,
                         List<MovimientoCronologico> movimientosCronologicos) {
}

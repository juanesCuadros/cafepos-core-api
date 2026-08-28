package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.FlujoCaja;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.util.List;

public record FlujoCajaResponse(@Monto BigDecimal saldoInicial, EntradasResponse entradas, SalidasResponse salidas,
                                 @Monto BigDecimal saldoFinal, @Monto BigDecimal diferencia,
                                 List<MovimientoCronologicoResponse> movimientosCronologicos) {

    public static FlujoCajaResponse de(FlujoCaja flujo) {
        EntradasResponse entradas = new EntradasResponse(flujo.ventasEfectivo(), flujo.ventasOtrosMetodos(),
                flujo.ingresosCaja());
        SalidasResponse salidas = new SalidasResponse(flujo.comprasPagadas(), flujo.gastosOperativos(),
                flujo.egresosCaja());
        return new FlujoCajaResponse(flujo.saldoInicial(), entradas, salidas, flujo.saldoFinal(),
                flujo.diferencia(), flujo.movimientosCronologicos().stream().map(MovimientoCronologicoResponse::de).toList());
    }
}

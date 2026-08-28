package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Fila de flujo_caja.movimientos_cronologicos — saldoAcumulado ya calculado (ver ContabilidadService.flujoCaja). */
public record MovimientoCronologico(OffsetDateTime fecha, String descripcion, String tipo, BigDecimal monto,
                                     BigDecimal saldoAcumulado) {

    public static final String TIPO_ENTRADA = "entrada";
    public static final String TIPO_SALIDA = "salida";
}

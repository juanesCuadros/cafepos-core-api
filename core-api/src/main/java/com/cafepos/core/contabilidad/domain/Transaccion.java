package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila de GET /contabilidad/transacciones. monto ya viene con el signo
 * real (negativo compra/gasto/egreso_caja, positivo venta/ingreso_caja)
 * — no hay campo de signo separado, ver api_13_contabilidad.md.
 */
public record Transaccion(OffsetDateTime fechaHora, String codigo, String tipo, String descripcion,
                           BigDecimal monto, String metodoPago, String usuario) {

    public static final String TIPO_VENTA = "venta";
    public static final String TIPO_COMPRA = "compra";
    public static final String TIPO_GASTO = "gasto";
    public static final String TIPO_EGRESO_CAJA = "egreso_caja";
    public static final String TIPO_INGRESO_CAJA = "ingreso_caja";
}

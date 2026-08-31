package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila de GET /contabilidad/transacciones. monto ya viene con el signo
 * real (negativo compra/gasto/egreso_caja, positivo venta/ingreso_caja)
 * — no hay campo de signo separado, ver api_13_contabilidad.md.
 * codigo es NULL para egreso_caja/ingreso_caja a proposito: caja_movimiento
 * no tiene columna codigo en el schema real, a diferencia de venta/compra/
 * gasto (si tienen). id cubre ese hueco SOLO para esos dos tipos (el id
 * real de caja_movimiento) — el frontend lo necesita como key estable de
 * fila, ya que codigo null colisionaba entre movimientos del mismo tipo
 * (ver INTEGRACION.md hallazgo 3.24). Para venta/compra/gasto va null: el
 * codigo ya es unico ahi, no hace falta.
 */
public record Transaccion(OffsetDateTime fechaHora, Integer id, String codigo, String tipo, String descripcion,
                           BigDecimal monto, String metodoPago, String usuario) {

    public static final String TIPO_VENTA = "venta";
    public static final String TIPO_COMPRA = "compra";
    public static final String TIPO_GASTO = "gasto";
    public static final String TIPO_EGRESO_CAJA = "egreso_caja";
    public static final String TIPO_INGRESO_CAJA = "ingreso_caja";
}

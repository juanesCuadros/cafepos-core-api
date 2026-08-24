package com.cafepos.core.caja.domain;

import java.math.BigDecimal;

/** Fila de resumen_por_metodo_pago (ver POST /caja/jornada/cerrar) — TODOS los metodos, no solo efectivo. */
public record ResumenMetodoPago(String metodo, BigDecimal total) {
}

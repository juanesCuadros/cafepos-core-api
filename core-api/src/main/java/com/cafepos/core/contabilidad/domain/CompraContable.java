package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila cruda de una compra pagada. fechaHora = compra.created_at (unico
 * TIMESTAMPTZ real disponible ahi) — compra.fecha es solo DATE (fecha de
 * negocio, usada para el filtro del rango, no para el orden cronologico
 * fino entre eventos del mismo dia).
 */
public record CompraContable(OffsetDateTime fechaHora, String codigo, BigDecimal total, String proveedorNombre,
                              String usuarioNombre) {
}

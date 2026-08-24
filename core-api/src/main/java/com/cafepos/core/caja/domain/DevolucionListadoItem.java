package com.cafepos.core.caja.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Fila de GET /devoluciones — join devolucion + venta + cliente, ver DevolucionRepository.listar. */
public record DevolucionListadoItem(Integer id, String ventaCodigo, OffsetDateTime fecha, String cliente,
                                     BigDecimal montoDevuelto, String metodoReembolso, String estado) {
}

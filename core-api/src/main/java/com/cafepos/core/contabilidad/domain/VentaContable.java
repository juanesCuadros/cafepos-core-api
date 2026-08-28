package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Fila cruda de una venta cobrada, antes de decidir la descripcion (distinta segun el endpoint que la usa). */
public record VentaContable(OffsetDateTime fechaHora, String codigo, BigDecimal total, String mesaNumero,
                             String usuarioNombre, String metodoPago) {
}

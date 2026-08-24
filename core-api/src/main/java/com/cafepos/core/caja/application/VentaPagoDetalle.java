package com.cafepos.core.caja.application;

import java.math.BigDecimal;

/** Un pago dentro de GET /ventas/{id} — ver HistorialVentasService.detalle. */
public record VentaPagoDetalle(String metodoPagoNombre, BigDecimal monto) {
}

package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.Venta;

/** Ver HistorialVentasService.anular. notaCreditoId null si notaCreditoGenerada es false. */
public record AnulacionResultado(Venta venta, boolean notaCreditoGenerada, Integer notaCreditoId) {
}

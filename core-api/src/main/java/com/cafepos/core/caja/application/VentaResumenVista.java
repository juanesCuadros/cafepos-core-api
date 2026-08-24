package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.Venta;

import java.util.List;

/** Fila de GET /ventas — ver HistorialVentasService.listar. */
public record VentaResumenVista(Venta venta, String cajeroNombre, List<String> metodosPago) {
}

package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.clientes.domain.ClienteRef;

/** Resultado de VentaService.cobrar — ver VentaController. */
public record VentaResultado(Venta venta, ClienteRef cliente, FacturaResumen factura) {
}

package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.clientes.domain.ClienteParaFactura;

import java.math.BigDecimal;

/** cliente null si la venta es de consumidor final (sin cliente_id). */
public record FacturaDetalleVista(FacturaDian factura, BigDecimal ventaTotal, ClienteParaFactura cliente) {
}

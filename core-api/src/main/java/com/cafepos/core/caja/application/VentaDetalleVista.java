package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.Venta;
import com.cafepos.core.clientes.domain.ClienteRef;
import com.cafepos.core.operacion.domain.PedidoItemParaVenta;

import java.util.List;

/** GET /ventas/{id} — ver HistorialVentasService.detalle. */
public record VentaDetalleVista(Venta venta, String cajeroNombre, ClienteRef cliente,
                                 List<PedidoItemParaVenta> items, List<VentaPagoDetalle> pagos,
                                 FacturaResumen factura) {
}

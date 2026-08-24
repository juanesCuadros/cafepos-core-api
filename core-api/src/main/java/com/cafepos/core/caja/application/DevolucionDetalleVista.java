package com.cafepos.core.caja.application;

import com.cafepos.core.caja.domain.Devolucion;
import com.cafepos.core.clientes.domain.ClienteRef;

import java.util.List;

/** cliente null si la venta es de consumidor final (sin cliente_id). */
public record DevolucionDetalleVista(Devolucion devolucion, String ventaCodigo, ClienteRef cliente,
                                      List<DevolucionItemDetalle> items) {
}

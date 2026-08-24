package com.cafepos.core.operacion.domain;

import java.math.BigDecimal;

/**
 * Datos minimos de un PedidoItem que com.cafepos.core.caja necesita para
 * calcular subtotal/impuestos al cobrar, y para resolver devoluciones
 * (matchear pedido_item_id + ver estadoPreparacion, ver RN-023/024 en
 * api_03_caja.md) — nunca la entidad PedidoItem completa. tasaImpuesto ya
 * resuelto (texto libre de producto.tasa_impuesto, null para items de
 * combo — un combo no tiene tasa propia, ver PedidoService.aItemParaVenta).
 *
 * @NamedInterface propio, ver PedidoParaVenta / PedidoService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("pedidoItemParaVenta")
public record PedidoItemParaVenta(Integer id, Integer productoId, Integer comboId, String nombre,
                                   BigDecimal cantidad, BigDecimal precioUnitario, String tasaImpuesto,
                                   String estadoPreparacion) {
}

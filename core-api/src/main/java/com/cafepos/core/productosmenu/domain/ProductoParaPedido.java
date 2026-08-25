package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;

/**
 * Datos minimos que com.cafepos.core.operacion necesita para agregar un
 * producto a un pedido — nunca la entidad Producto completa. tasaImpuesto
 * se agrega para que com.cafepos.core.caja calcule impuestos al cobrar
 * (ver Producto.tasaImpuesto — texto libre tipo "IVA 19%"/"INC 8%"/
 * "Exento", null = usa el default del tenant). codigo se agrega para que
 * com.cafepos.core.caja arme code_reference al transmitir la factura a
 * Factus (ver PedidoItemParaVenta/FacturaDianTransmisionService).
 *
 * @NamedInterface propio, ver ProductoService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("productoParaPedido")
public record ProductoParaPedido(Integer id, String nombre, BigDecimal precioVenta, String estado,
                                  Integer areaCocinaId, String tasaImpuesto, String codigo) {
}

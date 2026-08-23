package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;

/**
 * Datos minimos que com.cafepos.core.operacion necesita para agregar un
 * producto a un pedido — nunca la entidad Producto completa.
 *
 * @NamedInterface propio, ver ProductoService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("productoParaPedido")
public record ProductoParaPedido(Integer id, String nombre, BigDecimal precioVenta, String estado,
                                  Integer areaCocinaId) {
}

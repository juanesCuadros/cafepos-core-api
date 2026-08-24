package com.cafepos.core.clientes.domain;

/**
 * Referencia minima de un cliente para el response de una venta — id+nombre,
 * nunca la entidad Cliente completa.
 *
 * @NamedInterface propio, ver ClienteService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("clienteRef")
public record ClienteRef(Integer id, String nombre) {
}

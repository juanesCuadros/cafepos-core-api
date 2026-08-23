package com.cafepos.core.restaurante.domain;

/**
 * Fila de GET /zonas/{id}/mesas.
 *
 * @NamedInterface: expuesto puntualmente para que com.cafepos.core.operacion
 * lea/actualice el estado de una mesa (panel de mesas, abrir/mover pedido)
 * sin tocar la entidad Mesa completa — ver ZonaService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("mesaResumen")
public record MesaResumen(Integer id, String codigo, String numero, int capacidad, String estado) {
}

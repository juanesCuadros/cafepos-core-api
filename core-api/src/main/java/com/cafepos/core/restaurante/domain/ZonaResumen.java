package com.cafepos.core.restaurante.domain;

/**
 * Fila de GET /zonas — numMesas es calculado (COUNT), no una columna de zona.
 *
 * @NamedInterface: expuesto puntualmente para el panel de mesas de
 * com.cafepos.core.operacion (GET /operacion/mesas) — ver ZonaService.
 */
@org.springframework.modulith.NamedInterface("zonaResumen")
public record ZonaResumen(Integer id, String codigo, String icono, String nombre, long numMesas, String estado) {
}

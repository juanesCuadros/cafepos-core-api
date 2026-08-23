package com.cafepos.core.restaurante.domain;

/** Fila de GET /zonas — numMesas es calculado (COUNT), no una columna de zona. */
public record ZonaResumen(Integer id, String codigo, String icono, String nombre, long numMesas, String estado) {
}

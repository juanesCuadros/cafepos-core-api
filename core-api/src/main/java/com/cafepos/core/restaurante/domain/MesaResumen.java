package com.cafepos.core.restaurante.domain;

/** Fila de GET /zonas/{id}/mesas. */
public record MesaResumen(Integer id, String codigo, String numero, int capacidad, String estado) {
}

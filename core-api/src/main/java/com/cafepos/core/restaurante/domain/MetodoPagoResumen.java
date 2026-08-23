package com.cafepos.core.restaurante.domain;

/** Fila de GET /metodos-pago. */
public record MetodoPagoResumen(Integer id, String nombre, String icono, boolean esEfectivo, String estado) {
}

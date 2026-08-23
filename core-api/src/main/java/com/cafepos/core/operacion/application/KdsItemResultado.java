package com.cafepos.core.operacion.application;

/** Resultado de KdsService.cambiarEstadoItem — ver KdsController. */
public record KdsItemResultado(Integer id, String estadoPreparacion, boolean pedidoTodosListos) {
}

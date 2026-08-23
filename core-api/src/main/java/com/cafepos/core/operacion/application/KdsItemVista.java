package com.cafepos.core.operacion.application;

import java.math.BigDecimal;

/** Fila de un item dentro de GET /operacion/kds/pedidos — ver KdsService. */
public record KdsItemVista(Integer id, String nombre, BigDecimal cantidad, String observacion,
                            String estadoPreparacion) {
}

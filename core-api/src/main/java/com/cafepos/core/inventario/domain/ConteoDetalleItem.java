package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;

/** Fila anidada de GET /conteos/{id} y de la respuesta del POST — TODAS las filas, tengan o no diferencia. */
public record ConteoDetalleItem(String insumoNombre, BigDecimal stockSistema, BigDecimal stockFisico,
                                 BigDecimal diferencia) {
}

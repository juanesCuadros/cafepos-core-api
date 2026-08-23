package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;

/** Entrada de ConteoService.crear — una fila del detalle enviado en el POST, antes de resolverse contra Insumo. */
public record ConteoDetalleInput(Integer insumoId, BigDecimal stockFisico) {
}

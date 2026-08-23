package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;

/** Resultado de POST /inventario/ajustes — ver AjusteService.ajustar. */
public record AjusteResultado(Integer movimientoId, Integer insumoId, BigDecimal stockAnterior,
                               BigDecimal stockNuevo) {
}

package com.cafepos.core.inventario.domain;

import java.math.BigDecimal;

/** Resultado de POST /perdidas — ver PerdidaService.registrar. */
public record PerdidaResultado(Integer id, BigDecimal costoCalculado, BigDecimal stockAnterior,
                                BigDecimal stockNuevo) {
}

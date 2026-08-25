package com.cafepos.core.compras.application;

/** Resultado de POST /compras/{id}/anular — ver CompraService.anular. */
public record AnularCompraResultado(Integer id, String estado, int movimientosReversionGenerados) {
}

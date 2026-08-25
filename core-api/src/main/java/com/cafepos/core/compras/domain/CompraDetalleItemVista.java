package com.cafepos.core.compras.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Linea de detalle para GET /compras/{id} — insumo_codigo/insumo_nombre/unidad_medida aplanados, el join ya se hizo en SQL. */
public record CompraDetalleItemVista(Integer id, Integer insumoId, String insumoCodigo, String insumoNombre,
                                      String unidadMedida, BigDecimal cantidad, BigDecimal costoUnitario,
                                      String numeroLote, LocalDate fechaVencimiento, BigDecimal subtotal) {
}

package com.cafepos.core.compras.application;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Una linea del detalle de POST /compras — ver CompraService.registrar. */
public record DetalleCompraInput(Integer insumoId, BigDecimal cantidad, BigDecimal costoUnitario, String numeroLote,
                                  LocalDate fechaVencimiento) {
}

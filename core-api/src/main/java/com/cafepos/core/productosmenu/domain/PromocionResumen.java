package com.cafepos.core.productosmenu.domain;

import java.time.LocalDate;

/** Fila de GET /promociones. */
public record PromocionResumen(Integer id, String nombre, String tipoDescuento, LocalDate vigenciaInicio,
                                LocalDate vigenciaFin, String estado) {
}

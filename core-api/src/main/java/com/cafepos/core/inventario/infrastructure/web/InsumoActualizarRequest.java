package com.cafepos.core.inventario.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mismos campos que InsumoCrearRequest, todos opcionales. stockMaximo y
 * fechaVencimRef son JsonNullable (los unicos genuinamente nullable de
 * negocio en insumo). Sin stockActual ni costoActual a proposito — ni
 * siquiera existen como campos aca, esos solo cambian via ajustes,
 * perdidas, conteos o compras, nunca por PATCH directo.
 */
public record InsumoActualizarRequest(
        String nombre,
        Integer categoriaInsumoId,
        String unidadMedida,
        BigDecimal stockMinimo,
        JsonNullable<BigDecimal> stockMaximo,
        JsonNullable<LocalDate> fechaVencimRef,
        String estado) {
}

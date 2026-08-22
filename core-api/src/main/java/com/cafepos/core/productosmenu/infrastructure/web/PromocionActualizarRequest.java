package com.cafepos.core.productosmenu.infrastructure.web;

import jakarta.validation.constraints.Pattern;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Mismos campos que PromocionCrearRequest, todos opcionales — PATCH
 * actualiza solo lo que viene en el body. Los campos genuinamente nullable
 * de negocio (montoMinimo, cantidadMinima, horaInicio, horaFin, diasSemana)
 * son JsonNullable: ausente = no tocar, null explicito = borrar (ver regla
 * de DTOs de PATCH en CLAUDE.md).
 *
 * diasSemana: ausente = no tocar; null explicito o arreglo vacio ([]) = sin
 * restriccion de dia (limpia a NULL); arreglo con valores = reemplaza la
 * lista completa. Se eligio tratar [] igual que null (en vez de rechazarlo)
 * para no romper clientes existentes que ya mandaban [] para "limpiar".
 */
public record PromocionActualizarRequest(
        String nombre,

        @Pattern(regexp = "porcentaje|valor_fijo", message = "tipo_descuento debe ser 'porcentaje' o 'valor_fijo'")
        String tipoDescuento,

        BigDecimal valorDescuento,

        @Pattern(regexp = "producto|venta_total", message = "aplica_a debe ser 'producto' o 'venta_total'")
        String aplicaA,

        List<Integer> productosIds,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFin,
        JsonNullable<List<String>> diasSemana,
        JsonNullable<LocalTime> horaInicio,
        JsonNullable<LocalTime> horaFin,
        JsonNullable<Integer> cantidadMinima,
        JsonNullable<BigDecimal> montoMinimo,
        String estado) {
}

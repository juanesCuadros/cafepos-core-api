package com.cafepos.core.configuracion.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

/** modoComanda/propinaTipo NOT NULL en la tabla (tipo plano); el resto es nullable de negocio (JsonNullable). */
public record ConfiguracionSistemaActualizarRequest(
        String modoComanda,
        JsonNullable<Integer> tiempoLimitePrepMin,
        String propinaTipo,
        JsonNullable<BigDecimal> propinaPorcentaje,
        JsonNullable<String> propinaDestino,
        JsonNullable<BigDecimal> propinaPctMesero,
        JsonNullable<Integer> diasAnticipacionVencim,
        JsonNullable<String> estadoConexionDian,
        JsonNullable<BigDecimal> ivaPorcentaje,
        JsonNullable<BigDecimal> incPorcentaje) {
}

package com.cafepos.core.configuracion.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

public record ImpresoraActualizarRequest(
        JsonNullable<Integer> areaCocinaId,
        String tipo,
        String nombre,
        String tipoConexion,
        JsonNullable<String> ip,
        JsonNullable<Integer> puerto) {
}

package com.cafepos.core.operacion.infrastructure.web;

import jakarta.validation.constraints.Positive;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;

public record ItemEditarRequest(
        @Positive(message = "cantidad debe ser mayor a 0")
        BigDecimal cantidad,

        JsonNullable<String> observacion) {
}

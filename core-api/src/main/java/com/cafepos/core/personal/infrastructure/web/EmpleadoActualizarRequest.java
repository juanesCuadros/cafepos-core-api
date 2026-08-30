package com.cafepos.core.personal.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

/** Mismos campos que EmpleadoCrearRequest, todos opcionales. telefono es JsonNullable (nullable de negocio). */
public record EmpleadoActualizarRequest(String nombre, String cedula, String cargo,
                                         JsonNullable<String> telefono, String estado) {
}

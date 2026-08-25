package com.cafepos.core.compras.infrastructure.web;

import org.openapitools.jackson.nullable.JsonNullable;

/** Mismos campos que ProveedorCrearRequest, todos opcionales. contacto/telefono/correo/direccion son JsonNullable (ver DECISIONES YA TOMADAS). */
public record ProveedorActualizarRequest(String nombre, String nit, JsonNullable<String> contacto,
                                          JsonNullable<String> telefono, JsonNullable<String> correo,
                                          JsonNullable<String> direccion, String estado) {
}

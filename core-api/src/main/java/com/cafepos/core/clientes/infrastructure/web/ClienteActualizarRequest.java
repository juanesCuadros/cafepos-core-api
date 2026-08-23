package com.cafepos.core.clientes.infrastructure.web;

import jakarta.validation.constraints.Pattern;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Mismos campos que ClienteCrearRequest, todos opcionales. telefono,
 * correo y direccion son JsonNullable (los unicos genuinamente nullable
 * de negocio en cliente) — ver regla de DTOs de PATCH en CLAUDE.md.
 * tipoDocumento/numeroDocumento se quedan con el tipo plano a proposito:
 * si el cliente ya tiene ventas, ClienteService rechaza el intento de
 * cambiarlos ANTES de tocar el resto del body (ver contrato).
 */
public record ClienteActualizarRequest(
        String nombre,

        @Pattern(regexp = "CC|NIT", message = "tipo_documento debe ser 'CC' o 'NIT'")
        String tipoDocumento,

        String numeroDocumento,
        JsonNullable<String> telefono,
        JsonNullable<String> correo,
        JsonNullable<String> direccion) {
}

package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.RedesSociales;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Mismos campos que el GET, todos opcionales. Todos son JsonNullable salvo
 * nombreNegocio (el unico NOT NULL en la tabla restaurantes) — ver regla
 * de DTOs de PATCH en CLAUDE.md.
 */
public record RestauranteActualizarRequest(
        String nombreNegocio,
        JsonNullable<String> nit,
        JsonNullable<String> direccion,
        JsonNullable<String> departamento,
        JsonNullable<String> ciudad,
        JsonNullable<String> pais,
        JsonNullable<String> telefono,
        JsonNullable<String> correo,
        JsonNullable<String> telefonoRepresentante,
        JsonNullable<String> logoUrl,
        JsonNullable<RedesSociales> redesSociales,
        JsonNullable<String> descripcion) {
}

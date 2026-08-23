package com.cafepos.core.clientes.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** telefono/correo/direccion opcionales — el registro rapido del POS normalmente solo manda los primeros 3 campos. */
public record ClienteCrearRequest(
        @NotBlank(message = "tipo_documento es obligatorio")
        @Pattern(regexp = "CC|NIT", message = "tipo_documento debe ser 'CC' o 'NIT'")
        String tipoDocumento,

        @NotBlank(message = "numero_documento es obligatorio")
        String numeroDocumento,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String telefono,
        String correo,
        String direccion) {
}

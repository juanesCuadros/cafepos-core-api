package com.cafepos.admin.negocios.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** client_id/client_secret/username/password nunca se persisten en texto plano — ver shared.criptografia.FactusCredencialAttributeConverter. */
public record FacturacionDianRequest(
        @NotBlank(message = "El ambiente es obligatorio")
        @Pattern(regexp = "sandbox|produccion", message = "El ambiente debe ser 'sandbox' o 'produccion'")
        String ambiente,

        @NotBlank(message = "El client_id es obligatorio") String clientId,
        @NotBlank(message = "El client_secret es obligatorio") String clientSecret,
        @NotBlank(message = "El username es obligatorio") String username,
        @NotBlank(message = "El password es obligatorio") String password,
        @NotNull(message = "El rango de inicio es obligatorio") Long rangoInicio,
        @NotNull(message = "El rango de fin es obligatorio") Long rangoFin) {
}

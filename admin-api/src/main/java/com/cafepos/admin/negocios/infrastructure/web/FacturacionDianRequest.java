package com.cafepos.admin.negocios.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

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
        @NotNull(message = "El rango de fin es obligatorio") Long rangoFin,

        /*
         * Opcionales a proposito (ver INTEGRACION.md hallazgo 3.45): antes no
         * existian aca y la unica forma de setearlos era escribir a mano en la
         * base. Se dejan opcionales para no romper llamadas ya existentes que
         * no los mandan — si vienen null, no se pisa el valor ya guardado (ver
         * FacturacionDianResolucion.configurarCredencialesFactus). Si el
         * prefijo queda sin configurar, core-api cae a "FE" con un WARN.
         */
        String prefijo,
        LocalDate fechaExpedicion,
        LocalDate fechaVencimiento,

        /*
         * Id que Factus asigna al rango de numeracion (GET /v2/numbering-ranges,
         * campo "id" - NO es rangoInicio/rangoFin). Opcional en el request por
         * el mismo motivo que prefijo/fechas, pero en la practica core-api no
         * transmite ninguna factura a Factus sin esto configurado (ver V33 y
         * FacturacionDianService.credencialesFactusPara en core-api) - la
         * cuenta sandbox de este proyecto ya devolvio 422 sin el.
         */
        Long numberingRangeId) {
}

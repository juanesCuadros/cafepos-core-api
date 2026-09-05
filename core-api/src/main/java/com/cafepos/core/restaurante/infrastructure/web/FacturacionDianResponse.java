package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.FacturacionDianEstado;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/**
 * CRITICO DE SEGURIDAD: whitelist explicita campo por campo — client_id_factus
 * y client_secret_factus NUNCA deben aparecer aca, ni en FacturacionDianEstado
 * ni en FacturacionDianResolucion (ver Javadoc de esa entidad). No mapear la
 * entidad completa a proposito: si mañana se agrega un campo sensible nuevo
 * a la tabla, este DTO no lo expone salvo que alguien lo agregue aca a mano.
 *
 * "mensaje" solo viaja cuando NO esta configurado (@JsonInclude NON_NULL) —
 * ambos casos son 200, pero con forma de JSON distinta (ver contrato 10.4).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FacturacionDianResponse(String estadoConexion, String mensaje, String prefijo, Long rangoInicio,
                                       Long rangoFin, Long numeracionActual, LocalDate fechaExpedicion,
                                       LocalDate fechaVencimiento, String ambiente, String estado,
                                       Long numberingRangeId) {

    private static final String MENSAJE_NO_CONFIGURADO =
            "La facturación electrónica aún no está configurada. Contacta a soporte.";

    public static FacturacionDianResponse de(FacturacionDianEstado e) {
        if (!e.configurada()) {
            return new FacturacionDianResponse("inactiva", MENSAJE_NO_CONFIGURADO, null, null, null, null, null,
                    null, null, null, null);
        }
        return new FacturacionDianResponse(e.estadoConexion(), null, e.prefijo(), e.rangoInicio(), e.rangoFin(),
                e.numeracionActual(), e.fechaExpedicion(), e.fechaVencimiento(), e.ambiente(), e.estado(),
                e.numberingRangeId());
    }
}

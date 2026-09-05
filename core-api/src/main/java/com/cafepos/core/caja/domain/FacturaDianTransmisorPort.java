package com.cafepos.core.caja.domain;

/**
 * Puerto de salida hacia Factus real — implementado en infrastructure.factus
 * (com.cafepos.core.caja.infrastructure.factus.FactusFacturacionClienteAdapter).
 * Solo primitivos + tipos propios de este modulo en la firma (nunca
 * CredencialesFactus de com.cafepos.core.restaurante.domain) para que este
 * puerto, como el resto de domain, no dependa de otro modulo.
 */
public interface FacturaDianTransmisorPort {

    ResultadoTransmisionFactus transmitir(SolicitudTransmisionFactus solicitud, String clientId, String clientSecret,
                                           String username, String password, String ambiente, Long numberingRangeId);

    /** POST /v2/bills/{numeroFactura}/send-email — numeroFactura debe ser el numero REAL ya asignado por Factus, nunca el local. */
    ResultadoEnvioCorreoFactus enviarCorreo(String numeroFactura, String email, String clientId, String clientSecret,
                                             String username, String password, String ambiente);
}

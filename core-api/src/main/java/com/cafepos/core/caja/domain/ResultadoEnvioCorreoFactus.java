package com.cafepos.core.caja.domain;

/** Resultado real de POST /v2/bills/{numero}/send-email — ver FacturaDianTransmisorPort.enviarCorreo. */
public record ResultadoEnvioCorreoFactus(boolean exitoso, String mensajeError) {
}

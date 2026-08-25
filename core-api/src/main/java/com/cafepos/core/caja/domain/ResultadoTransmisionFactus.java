package com.cafepos.core.caja.domain;

/**
 * Resultado de un intento de transmision a Factus. exitoso=true significa
 * que Factus devolvio una respuesta usable (2xx de POST /v2/bills/validate)
 * — independiente de validado, que refleja is_validated de esa respuesta
 * (true -> factura_dian.estado_dian pasa a 'aceptada', false -> 'rechazada',
 * en ambos casos con numeroFactura/cufe/qrCode reales de Factus). exitoso=false
 * (fallo de autenticacion, timeout, red, HTTP no-2xx) significa que
 * factura_dian queda TAL CUAL estaba — ver FacturaDianTransmisionService.
 */
public record ResultadoTransmisionFactus(boolean exitoso, String numeroFactura, String cufe, String qrCode,
                                          boolean validado, String mensajeError) {
}

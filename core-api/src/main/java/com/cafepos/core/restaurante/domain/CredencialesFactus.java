package com.cafepos.core.restaurante.domain;

/**
 * Credenciales Factus descifradas de la resolucion DIAN vigente del tenant
 * actual — SOLO para el cliente real de Factus (com.cafepos.core.caja.infrastructure.factus).
 * ambiente viaja en el vocabulario de la columna ('pruebas'/'produccion',
 * ver V1__schema_v4.sql), no en el vocabulario de Factus ('sandbox'/
 * 'produccion') — el cliente Factus interpreta "produccion" como la unica
 * senal de usar la URL base de produccion, igual que FactusProbarConexionService.
 *
 * @NamedInterface propio, ver FacturacionDianService.credencialesFactusPara
 * (tambien anotado) — nunca se expone la entidad FacturacionDianResolucion
 * completa para esto.
 */
@org.springframework.modulith.NamedInterface("credencialesFactus")
public record CredencialesFactus(String clientId, String clientSecret, String username, String password,
                                  String ambiente, Long numberingRangeId) {
}

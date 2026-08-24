package com.cafepos.core.restaurante.domain;

/**
 * Resultado de reservar (incrementar) el siguiente numero de secuencia de
 * la resolucion DIAN vigente del tenant — prefijo REAL de esa resolucion
 * especifica (distintos tenants pueden tener prefijos distintos
 * configurados por la DIAN), nunca un prefijo hardcodeado en el modulo
 * que consume esto (com.cafepos.core.caja).
 *
 * @NamedInterface propio, ver FacturacionDianService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("numeroFacturaReservado")
public record NumeroFacturaReservado(Integer resolucionId, String prefijo, Integer numero) {
}

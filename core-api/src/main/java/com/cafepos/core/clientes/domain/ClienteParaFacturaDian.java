package com.cafepos.core.clientes.domain;

/**
 * Datos de un cliente para transmitir una factura DIAN real a Factus
 * (com.cafepos.core.caja.application.FacturaDianTransmisionService) —
 * numeroDocumento SIN mascara (valor real, Factus lo exige tal cual),
 * a diferencia de ClienteParaFactura (que enmascara para el detalle/reenvio
 * visible al usuario). Exclusivo para ese caller puntual.
 *
 * @NamedInterface propio, ver ClienteService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("clienteParaFacturaDian")
public record ClienteParaFacturaDian(Integer id, String tipoDocumento, String numeroDocumento, String nombre,
                                      String correo) {
}

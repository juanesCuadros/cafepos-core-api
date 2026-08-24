package com.cafepos.core.clientes.domain;

/**
 * Datos minimos de un cliente para el detalle/reenvio de una factura DIAN
 * (com.cafepos.core.caja) — numero_documento YA enmascarado aca, nunca el
 * crudo, y correo para el stub de reenviar-correo.
 *
 * @NamedInterface propio, ver ClienteService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("clienteParaFactura")
public record ClienteParaFactura(Integer id, String nombre, String numeroDocumentoEnmascarado, String correo) {
}

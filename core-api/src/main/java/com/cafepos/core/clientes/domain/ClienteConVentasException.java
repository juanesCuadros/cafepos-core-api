package com.cafepos.core.clientes.domain;

/**
 * PATCH /clientes/{id} rechazado — intento de cambiar tipo_documento o
 * numero_documento en un cliente que ya tiene ventas registradas (evita
 * romper la trazabilidad de facturas ya emitidas a ese documento). Se
 * valida ANTES de aplicar ningun cambio del body — todo o nada, ver
 * ClienteService.actualizar.
 */
public class ClienteConVentasException extends RuntimeException {

    public ClienteConVentasException() {
        super("No se puede cambiar el documento de un cliente con ventas registradas");
    }
}

package com.cafepos.core.caja.domain;

/** POST /facturas/{id}/reintentar-envio solo aplica si estado_dian es 'pendiente' o 'rechazada'. */
public class EstadoFacturaInvalidoException extends RuntimeException {

    public EstadoFacturaInvalidoException() {
        super("Solo se puede reintentar el envio de una factura pendiente o rechazada");
    }
}

package com.cafepos.core.restaurante.domain;

/** DELETE /zonas/{id} rechazado — la zona tiene mesas asociadas, ver ZonaService.eliminar. */
public class ZonaConMesasException extends RuntimeException {

    public ZonaConMesasException(long numMesas) {
        super("No se puede eliminar, esta zona tiene " + numMesas + " mesas asociadas");
    }
}

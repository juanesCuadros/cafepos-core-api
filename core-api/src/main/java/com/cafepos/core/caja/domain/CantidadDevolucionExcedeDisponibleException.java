package com.cafepos.core.caja.domain;

import java.math.BigDecimal;

/**
 * La cantidad solicitada para devolver un pedido_item, sumada a lo que ya se
 * devolvio antes de ese mismo item (en esta u otras devoluciones previas
 * sobre la misma venta), supera lo que realmente se compro — evita devolver
 * mas unidades de las vendidas, incluida la misma unidad dos veces.
 */
public class CantidadDevolucionExcedeDisponibleException extends RuntimeException {

    public CantidadDevolucionExcedeDisponibleException(Integer pedidoItemId, BigDecimal disponible) {
        super("No se puede devolver esa cantidad del item " + pedidoItemId + ": solo quedan " + disponible
                + " unidad(es) disponibles para devolver");
    }
}

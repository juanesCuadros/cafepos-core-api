package com.cafepos.core.caja.domain;

/** promociones_aplicadas[].promocion_id no existe o no esta activa — mismo mensaje para ambos casos. */
public class PromocionNoEncontradaException extends RuntimeException {

    public PromocionNoEncontradaException() {
        super("Promoción no encontrada o inactiva");
    }
}

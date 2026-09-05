package com.cafepos.core.caja.domain;

/** Esta factura ya tiene una nota de crédito generada — no se puede generar otra. */
public class NotaCreditoYaExisteException extends RuntimeException {

    public NotaCreditoYaExisteException() {
        super("Esta factura ya tiene una nota de crédito generada");
    }
}

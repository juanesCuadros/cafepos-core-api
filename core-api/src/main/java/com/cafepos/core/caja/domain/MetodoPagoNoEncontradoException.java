package com.cafepos.core.caja.domain;

/** pagos[].metodo_pago_id no existe para este tenant. */
public class MetodoPagoNoEncontradoException extends RuntimeException {

    public MetodoPagoNoEncontradoException() {
        super("Método de pago no encontrado");
    }
}

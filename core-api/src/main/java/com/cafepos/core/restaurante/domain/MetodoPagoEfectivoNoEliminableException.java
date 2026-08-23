package com.cafepos.core.restaurante.domain;

/** DELETE /metodos-pago/{id} rechazado — es el registro es_efectivo=true, ver MetodoPagoService.eliminar. */
public class MetodoPagoEfectivoNoEliminableException extends RuntimeException {

    public MetodoPagoEfectivoNoEliminableException() {
        super("El método de pago Efectivo no se puede eliminar");
    }
}

package com.cafepos.core.restaurante.domain;

/** PATCH /metodos-pago/{id} rechazado — es el registro es_efectivo=true, ver MetodoPagoService.actualizar. */
public class MetodoPagoEfectivoNoDesactivableException extends RuntimeException {

    public MetodoPagoEfectivoNoDesactivableException() {
        super("El método de pago Efectivo no se puede desactivar");
    }
}

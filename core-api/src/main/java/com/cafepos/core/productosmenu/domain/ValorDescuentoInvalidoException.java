package com.cafepos.core.productosmenu.domain;

/** valor_descuento fuera de rango para el tipo_descuento indicado (ver PromocionService.validarValorDescuento). */
public class ValorDescuentoInvalidoException extends RuntimeException {

    public ValorDescuentoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

package com.cafepos.core.compras.domain;

/** Revertir el stock de una linea de la compra dejaria stock_actual negativo — ver InsumoService.revertirPorAnulacionCompra. */
public class StockInsuficienteParaAnularException extends RuntimeException {

    public StockInsuficienteParaAnularException() {
        super("No se puede anular: revertir el stock dejaria una cantidad negativa (ya se consumio o vendio parte de esta compra)");
    }
}

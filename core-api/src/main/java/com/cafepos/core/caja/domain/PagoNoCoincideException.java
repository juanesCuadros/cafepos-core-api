package com.cafepos.core.caja.domain;

/** Suma de pagos[].monto != total calculado (con tolerancia de centavos, ver VentaService). Mensaje exacto del contrato. */
public class PagoNoCoincideException extends RuntimeException {

    public PagoNoCoincideException() {
        super("La suma de los métodos de pago no coincide con el total de la venta");
    }
}

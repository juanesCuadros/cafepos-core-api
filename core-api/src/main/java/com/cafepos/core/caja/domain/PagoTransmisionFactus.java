package com.cafepos.core.caja.domain;

import java.math.BigDecimal;

/** Un venta_pago ya mapeado al vocabulario de Factus — ver FacturaDianTransmisionService. payment_form es siempre "1" (contado), lo fija el adapter. */
public record PagoTransmisionFactus(String paymentMethodCode, BigDecimal monto) {
}

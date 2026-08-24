package com.cafepos.core.caja.application;

import java.math.BigDecimal;

/** Un elemento de pagos del request de POST /ventas. */
public record PagoInput(Integer metodoPagoId, BigDecimal monto) {
}

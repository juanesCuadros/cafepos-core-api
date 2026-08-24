package com.cafepos.core.caja.application;

import java.math.BigDecimal;

/** Un elemento de promociones_aplicadas del request de POST /ventas. */
public record PromocionAplicadaInput(Integer promocionId, BigDecimal montoDescuento) {
}

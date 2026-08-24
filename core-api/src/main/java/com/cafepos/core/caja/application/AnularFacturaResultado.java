package com.cafepos.core.caja.application;

import java.math.BigDecimal;

public record AnularFacturaResultado(Integer notaCreditoId, Integer facturaId, BigDecimal monto) {
}

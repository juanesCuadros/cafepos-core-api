package com.cafepos.core.caja.application;

import java.math.BigDecimal;

public record ItemDevolucionInput(Integer pedidoItemId, BigDecimal cantidad) {
}

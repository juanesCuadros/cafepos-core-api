package com.cafepos.core.caja.domain;

import java.math.BigDecimal;

/** Un item de venta ya mapeado al vocabulario de Factus — ver FacturaDianTransmisionService. */
public record ItemTransmisionFactus(String codeReference, String nombre, BigDecimal cantidad, BigDecimal precio,
                                     String taxCode, BigDecimal taxRate) {
}

package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AperturaJornada(OffsetDateTime fechaApertura, BigDecimal montoInicial) {
}

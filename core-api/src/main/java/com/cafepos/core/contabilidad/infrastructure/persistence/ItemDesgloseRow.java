package com.cafepos.core.contabilidad.infrastructure.persistence;

import java.math.BigDecimal;

interface ItemDesgloseRow {

    String getNombre();

    BigDecimal getTotal();
}

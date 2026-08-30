package com.cafepos.core.contabilidad.domain;

import java.math.BigDecimal;

/** Fila de cualquiera de los 3 desgloses de Balance general (por metodo de pago, proveedor o categoria). */
public record ItemDesglose(String nombre, BigDecimal total) {
}

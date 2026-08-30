package com.cafepos.core.personal.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Una venta 'cobrado' cuyo pedido.usuario_id corresponde al usuario asociado a un empleado — ver PropinaRepository. */
public record VentaConPropina(String codigo, OffsetDateTime fecha, BigDecimal propina) {
}

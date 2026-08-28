package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.Transaccion;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** monto ya viene con signo real (negativo compra/gasto/egreso_caja, positivo venta/ingreso_caja). */
public record TransaccionResponse(OffsetDateTime fechaHora, String codigo, String tipo, String descripcion,
                                   @Monto BigDecimal monto, String metodoPago, String usuario) {

    public static TransaccionResponse de(Transaccion t) {
        return new TransaccionResponse(t.fechaHora(), t.codigo(), t.tipo(), t.descripcion(), t.monto(),
                t.metodoPago(), t.usuario());
    }
}

package com.cafepos.core.contabilidad.infrastructure.web;

import com.cafepos.core.contabilidad.domain.Transaccion;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * monto ya viene con signo real (negativo compra/gasto/egreso_caja,
 * positivo venta/ingreso_caja). id solo viene no-null para
 * egreso_caja/ingreso_caja (ver Transaccion, hallazgo 3.24) — el frontend
 * lo usa como key de fila cuando codigo es null.
 */
public record TransaccionResponse(OffsetDateTime fechaHora, Integer id, String codigo, String tipo,
                                   String descripcion, @Monto BigDecimal monto, String metodoPago, String usuario) {

    public static TransaccionResponse de(Transaccion t) {
        return new TransaccionResponse(t.fechaHora(), t.id(), t.codigo(), t.tipo(), t.descripcion(), t.monto(),
                t.metodoPago(), t.usuario());
    }
}

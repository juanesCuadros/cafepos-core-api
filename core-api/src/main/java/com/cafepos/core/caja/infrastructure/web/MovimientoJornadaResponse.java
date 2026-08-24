package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.MovimientoVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MovimientoJornadaResponse(Integer id, String tipo, @Monto BigDecimal monto, String motivo,
                                         String usuario, OffsetDateTime fechaHora) {

    public static MovimientoJornadaResponse de(MovimientoVista vista) {
        var m = vista.movimiento();
        return new MovimientoJornadaResponse(m.getId(), m.getTipo(), m.getMonto(), m.getMotivo(),
                vista.usuarioNombre(), m.getFechaHora());
    }
}

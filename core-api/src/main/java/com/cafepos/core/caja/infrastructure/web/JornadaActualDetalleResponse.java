package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.JornadaActualVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record JornadaActualDetalleResponse(Integer id, UsuarioAperturaResponse usuarioApertura,
                                            OffsetDateTime fechaApertura, @Monto BigDecimal montoInicial,
                                            @Monto BigDecimal totalVentasActual,
                                            List<MovimientoJornadaResponse> movimientos) {

    public static JornadaActualDetalleResponse de(JornadaActualVista vista) {
        var j = vista.jornada();
        return new JornadaActualDetalleResponse(j.getId(), UsuarioAperturaResponse.de(vista.usuarioApertura()),
                j.getFechaApertura(), j.getMontoInicial(), vista.totalVentasActual(),
                vista.movimientos().stream().map(MovimientoJornadaResponse::de).toList());
    }
}

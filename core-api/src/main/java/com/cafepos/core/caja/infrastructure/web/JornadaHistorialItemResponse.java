package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.JornadaArqueoVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record JornadaHistorialItemResponse(Integer id, OffsetDateTime fechaApertura, OffsetDateTime fechaCierre,
                                            String usuarioApertura, String usuarioCierre,
                                            @Monto BigDecimal montoInicial, @Monto BigDecimal totalVentas,
                                            @Monto BigDecimal diferencia) {

    public static JornadaHistorialItemResponse de(JornadaArqueoVista vista) {
        var j = vista.jornada();
        return new JornadaHistorialItemResponse(j.getId(), j.getFechaApertura(), j.getFechaCierre(),
                vista.usuarioAperturaNombre(), vista.usuarioCierreNombre(), j.getMontoInicial(), vista.totalVentas(),
                j.getDiferencia());
    }
}

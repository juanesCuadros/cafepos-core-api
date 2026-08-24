package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.domain.CajaJornada;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record JornadaAbiertaResponse(Integer id, Integer usuarioAperturaId, OffsetDateTime fechaApertura,
                                      @Monto BigDecimal montoInicial, String estado) {

    public static JornadaAbiertaResponse de(CajaJornada jornada) {
        return new JornadaAbiertaResponse(jornada.getId(), jornada.getUsuarioAperturaId(), jornada.getFechaApertura(),
                jornada.getMontoInicial(), jornada.getEstado());
    }
}

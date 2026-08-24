package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.JornadaArqueoVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record CerrarJornadaResponse(Integer id, OffsetDateTime fechaCierre, @Monto BigDecimal montoInicial,
                                     @Monto BigDecimal montoFinalSistema, @Monto BigDecimal montoFinalFisico,
                                     @Monto BigDecimal diferencia, String estado,
                                     List<ResumenMetodoPagoResponse> resumenPorMetodoPago) {

    public static CerrarJornadaResponse de(JornadaArqueoVista vista) {
        var j = vista.jornada();
        return new CerrarJornadaResponse(j.getId(), j.getFechaCierre(), j.getMontoInicial(), j.getMontoFinalSistema(),
                j.getMontoFinalFisico(), j.getDiferencia(), j.getEstado(),
                vista.resumenPorMetodoPago().stream().map(ResumenMetodoPagoResponse::de).toList());
    }
}

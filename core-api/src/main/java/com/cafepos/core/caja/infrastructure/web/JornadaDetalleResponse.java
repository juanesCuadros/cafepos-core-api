package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.JornadaArqueoVista;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** GET /caja/jornadas/{id} — misma estructura que el arqueo de POST cerrar, mas todos los movimientos. */
public record JornadaDetalleResponse(Integer id, OffsetDateTime fechaApertura, OffsetDateTime fechaCierre,
                                      String usuarioApertura, String usuarioCierre, @Monto BigDecimal montoInicial,
                                      @Monto BigDecimal montoFinalSistema, @Monto BigDecimal montoFinalFisico,
                                      @Monto BigDecimal diferencia, String estado,
                                      List<ResumenMetodoPagoResponse> resumenPorMetodoPago,
                                      List<MovimientoJornadaResponse> movimientos) {

    public static JornadaDetalleResponse de(JornadaArqueoVista vista) {
        var j = vista.jornada();
        return new JornadaDetalleResponse(j.getId(), j.getFechaApertura(), j.getFechaCierre(),
                vista.usuarioAperturaNombre(), vista.usuarioCierreNombre(), j.getMontoInicial(),
                j.getMontoFinalSistema(), j.getMontoFinalFisico(), j.getDiferencia(), j.getEstado(),
                vista.resumenPorMetodoPago().stream().map(ResumenMetodoPagoResponse::de).toList(),
                vista.movimientos().stream().map(MovimientoJornadaResponse::de).toList());
    }
}

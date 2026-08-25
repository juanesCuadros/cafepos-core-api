package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MetodoPagoResumen;

public record MetodoPagoListItemResponse(Integer id, String nombre, String icono, boolean esEfectivo,
                                          String estado, String codigoFactus) {

    public static MetodoPagoListItemResponse de(MetodoPagoResumen resumen) {
        return new MetodoPagoListItemResponse(resumen.id(), resumen.nombre(), resumen.icono(),
                resumen.esEfectivo(), resumen.estado(), resumen.codigoFactus());
    }
}

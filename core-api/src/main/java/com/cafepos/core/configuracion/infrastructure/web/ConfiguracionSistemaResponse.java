package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.ConfiguracionSistema;

import java.math.BigDecimal;

public record ConfiguracionSistemaResponse(String modoComanda, Integer tiempoLimitePrepMin, String propinaTipo,
                                            BigDecimal propinaPorcentaje, String propinaDestino,
                                            BigDecimal propinaPctMesero, Integer diasAnticipacionVencim,
                                            String estadoConexionDian, BigDecimal ivaPorcentaje,
                                            BigDecimal incPorcentaje) {

    public static ConfiguracionSistemaResponse de(ConfiguracionSistema c) {
        return new ConfiguracionSistemaResponse(c.getModoComanda(), c.getTiempoLimitePrepMin(), c.getPropinaTipo(),
                c.getPropinaPorcentaje(), c.getPropinaDestino(), c.getPropinaPctMesero(),
                c.getDiasAnticipacionVencim(), c.getEstadoConexionDian(), c.getIvaPorcentaje(), c.getIncPorcentaje());
    }
}

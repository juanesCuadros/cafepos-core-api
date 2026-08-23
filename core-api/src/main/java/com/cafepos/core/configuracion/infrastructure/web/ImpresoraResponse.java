package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.Impresora;

public record ImpresoraResponse(Integer id, Integer areaCocinaId, String tipo, String nombre, String tipoConexion,
                                 String ip, Integer puerto) {

    public static ImpresoraResponse de(Impresora impresora) {
        return new ImpresoraResponse(impresora.getId(), impresora.getAreaCocinaId(), impresora.getTipo(),
                impresora.getNombre(), impresora.getTipoConexion(), impresora.getIp(), impresora.getPuerto());
    }
}

package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.ZonaPanel;

import java.util.List;

public record ZonaMesasResponse(Integer id, String codigo, String icono, String nombre,
                                 List<MesaEstadoResponse> mesas) {

    public static ZonaMesasResponse de(ZonaPanel zona) {
        return new ZonaMesasResponse(zona.id(), zona.codigo(), zona.icono(), zona.nombre(),
                zona.mesas().stream().map(MesaEstadoResponse::de).toList());
    }
}

package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.ZonaPanel;

import java.util.List;

public record MesasPanelResponse(List<ZonaMesasResponse> zonas) {

    public static MesasPanelResponse de(List<ZonaPanel> zonas) {
        return new MesasPanelResponse(zonas.stream().map(ZonaMesasResponse::de).toList());
    }
}

package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.MesaPanel;

import java.time.OffsetDateTime;

public record MesaEstadoResponse(Integer id, String codigo, String numero, int capacidad, String estado,
                                  Integer pedidoId, OffsetDateTime ocupadaDesde) {

    public static MesaEstadoResponse de(MesaPanel mesa) {
        return new MesaEstadoResponse(mesa.id(), mesa.codigo(), mesa.numero(), mesa.capacidad(), mesa.estado(),
                mesa.pedidoId(), mesa.ocupadaDesde());
    }
}

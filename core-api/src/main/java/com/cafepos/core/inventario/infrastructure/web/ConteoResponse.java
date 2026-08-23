package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.ConteoCompleto;

import java.time.OffsetDateTime;
import java.util.List;

/** Respuesta de POST /conteos y GET /conteos/{id} — misma forma en ambos casos. */
public record ConteoResponse(Integer id, String usuario, OffsetDateTime fecha,
                              List<ConteoDetalleItemResponse> detalle) {

    public static ConteoResponse de(ConteoCompleto completo) {
        return new ConteoResponse(completo.id(), completo.usuarioNombre(), completo.fecha(),
                completo.detalle().stream().map(ConteoDetalleItemResponse::de).toList());
    }
}

package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.restaurante.domain.MesaResumen;

public record MesaRefResponse(Integer id, String numero) {

    public static MesaRefResponse de(MesaResumen mesa) {
        return new MesaRefResponse(mesa.id(), mesa.numero());
    }
}

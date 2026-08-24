package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.restaurante.domain.MesaResumen;

public record MesaRefResponse(Integer id, String numero) {

    /** null si mesa es null — un pedido tipo='venta_rapida' no tiene mesa asociada. */
    public static MesaRefResponse de(MesaResumen mesa) {
        return mesa == null ? null : new MesaRefResponse(mesa.id(), mesa.numero());
    }
}

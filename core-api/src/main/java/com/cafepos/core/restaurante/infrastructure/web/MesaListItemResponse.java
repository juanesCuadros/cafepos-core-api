package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MesaResumen;

public record MesaListItemResponse(Integer id, String codigo, String numero, int capacidad, String estado) {

    public static MesaListItemResponse de(MesaResumen resumen) {
        return new MesaListItemResponse(resumen.id(), resumen.codigo(), resumen.numero(), resumen.capacidad(),
                resumen.estado());
    }
}

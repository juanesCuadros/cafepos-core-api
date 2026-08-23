package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.ZonaResumen;

public record ZonaListItemResponse(Integer id, String codigo, String icono, String nombre, long numMesas,
                                    String estado) {

    public static ZonaListItemResponse de(ZonaResumen resumen) {
        return new ZonaListItemResponse(resumen.id(), resumen.codigo(), resumen.icono(), resumen.nombre(),
                resumen.numMesas(), resumen.estado());
    }
}

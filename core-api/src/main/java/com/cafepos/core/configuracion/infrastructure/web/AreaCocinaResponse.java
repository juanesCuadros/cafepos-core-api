package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.AreaCocina;

public record AreaCocinaResponse(Integer id, String nombre, String estado) {

    public static AreaCocinaResponse de(AreaCocina areaCocina) {
        return new AreaCocinaResponse(areaCocina.getId(), areaCocina.getNombre(), areaCocina.getEstado());
    }
}

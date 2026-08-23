package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.AreaCocina;

import java.util.List;

/** clave "areas" (no "areas_cocina") — confirmado contra api_11_configuracion.md 11.3. */
public record AreasCocinaResponse(List<AreaCocinaResponse> areas) {

    public static AreasCocinaResponse de(List<AreaCocina> areas) {
        return new AreasCocinaResponse(areas.stream().map(AreaCocinaResponse::de).toList());
    }
}

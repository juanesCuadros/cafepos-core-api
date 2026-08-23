package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.Zona;

/** Respuesta de POST/PATCH /zonas — misma forma minima que especifica el contrato para el POST. */
public record ZonaCreadoResponse(Integer id, String codigo, String nombre) {

    public static ZonaCreadoResponse de(Zona zona) {
        return new ZonaCreadoResponse(zona.getId(), zona.getCodigo(), zona.getNombre());
    }
}

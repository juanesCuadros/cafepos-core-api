package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.Mesa;

/** Respuesta de POST/PATCH /mesas — misma forma minima que especifica el contrato para el POST. */
public record MesaCreadoResponse(Integer id, String codigo, String numero) {

    public static MesaCreadoResponse de(Mesa mesa) {
        return new MesaCreadoResponse(mesa.getId(), mesa.getCodigo(), mesa.getNumero());
    }
}

package com.cafepos.core.restaurante.infrastructure.web;

import com.cafepos.core.restaurante.domain.MetodoPago;

/** Respuesta de POST/PATCH /metodos-pago — misma forma minima que especifica el contrato para el POST. */
public record MetodoPagoCreadoResponse(Integer id, String nombre) {

    public static MetodoPagoCreadoResponse de(MetodoPago metodoPago) {
        return new MetodoPagoCreadoResponse(metodoPago.getId(), metodoPago.getNombre());
    }
}

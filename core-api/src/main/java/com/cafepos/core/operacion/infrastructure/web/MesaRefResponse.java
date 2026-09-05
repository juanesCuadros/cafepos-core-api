package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.restaurante.domain.MesaResumen;

/**
 * `estado` agregado para que el frontend pueda saber si la mesa quedo en
 * 'lista_cobrar' (marcarListaCobrar/prefactura) sin pedir el panel de mesas
 * aparte — antes se perdia en el mapeo, aunque MesaResumen ya lo trae.
 */
public record MesaRefResponse(Integer id, String numero, String estado) {

    /** null si mesa es null — un pedido tipo='venta_rapida' no tiene mesa asociada. */
    public static MesaRefResponse de(MesaResumen mesa) {
        return mesa == null ? null : new MesaRefResponse(mesa.id(), mesa.numero(), mesa.estado());
    }
}

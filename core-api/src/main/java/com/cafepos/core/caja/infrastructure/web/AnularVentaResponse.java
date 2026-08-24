package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.AnulacionResultado;

public record AnularVentaResponse(Integer id, String estado, boolean notaCreditoGenerada, Integer notaCreditoId) {

    public static AnularVentaResponse de(AnulacionResultado resultado) {
        return new AnularVentaResponse(resultado.venta().getId(), resultado.venta().getEstado(),
                resultado.notaCreditoGenerada(), resultado.notaCreditoId());
    }
}

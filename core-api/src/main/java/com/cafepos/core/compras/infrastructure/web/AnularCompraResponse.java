package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.application.AnularCompraResultado;

public record AnularCompraResponse(Integer id, String estado, String mensaje, int movimientosReversionGenerados) {

    public static AnularCompraResponse de(AnularCompraResultado r) {
        return new AnularCompraResponse(r.id(), r.estado(), "Compra anulada. Stock y costos revertidos.",
                r.movimientosReversionGenerados());
    }
}

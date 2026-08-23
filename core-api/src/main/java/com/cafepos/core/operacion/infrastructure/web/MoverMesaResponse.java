package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.MoverMesaResultado;

public record MoverMesaResponse(String mensaje, Integer mesaAnteriorId, Integer mesaNuevaId) {

    public static MoverMesaResponse de(MoverMesaResultado resultado) {
        return new MoverMesaResponse("Pedido movido correctamente", resultado.mesaAnteriorId(),
                resultado.mesaNuevaId());
    }
}

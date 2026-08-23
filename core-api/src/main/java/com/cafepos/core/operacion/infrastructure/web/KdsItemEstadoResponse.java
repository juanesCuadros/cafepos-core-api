package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.KdsItemResultado;

public record KdsItemEstadoResponse(Integer id, String estadoPreparacion, boolean pedidoTodosListos) {

    public static KdsItemEstadoResponse de(KdsItemResultado resultado) {
        return new KdsItemEstadoResponse(resultado.id(), resultado.estadoPreparacion(),
                resultado.pedidoTodosListos());
    }
}

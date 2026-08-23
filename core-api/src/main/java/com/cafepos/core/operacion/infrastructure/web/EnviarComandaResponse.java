package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.operacion.application.EnviarComandaResultado;

import java.util.List;

public record EnviarComandaResponse(String mensaje, String modo, List<Integer> itemsEnviados) {

    public static EnviarComandaResponse de(EnviarComandaResultado resultado) {
        return new EnviarComandaResponse("Comanda enviada", resultado.modo(), resultado.itemsEnviados());
    }
}

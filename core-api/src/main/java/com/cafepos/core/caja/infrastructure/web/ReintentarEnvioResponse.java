package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.ReintentarEnvioResultado;

public record ReintentarEnvioResponse(Integer id, String estadoDian, String mensaje) {

    public static ReintentarEnvioResponse de(ReintentarEnvioResultado resultado) {
        return new ReintentarEnvioResponse(resultado.id(), resultado.estadoDian(), resultado.mensaje());
    }
}

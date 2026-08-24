package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.caja.application.ReenviarCorreoResultado;

public record ReenviarCorreoResponse(String mensaje) {

    public static ReenviarCorreoResponse de(ReenviarCorreoResultado resultado) {
        return new ReenviarCorreoResponse(resultado.mensaje());
    }
}

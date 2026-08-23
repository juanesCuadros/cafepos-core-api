package com.cafepos.core.shared.seguridad;

public record PinVerificarResponse(boolean autorizado, Integer usuarioAutorizaId, String pinToken) {

    public static PinVerificarResponse de(PinAutorizacion autorizacion) {
        return new PinVerificarResponse(true, autorizacion.usuarioAutorizaId(), autorizacion.pinToken());
    }
}

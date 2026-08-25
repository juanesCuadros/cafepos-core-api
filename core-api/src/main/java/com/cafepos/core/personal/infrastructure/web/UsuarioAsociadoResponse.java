package com.cafepos.core.personal.infrastructure.web;

import com.cafepos.core.personal.domain.UsuarioAsociado;

public record UsuarioAsociadoResponse(Integer id, String correo, String rol) {

    public static UsuarioAsociadoResponse de(UsuarioAsociado u) {
        return u == null ? null : new UsuarioAsociadoResponse(u.id(), u.correo(), u.rol());
    }
}

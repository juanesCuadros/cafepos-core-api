package com.cafepos.core.operacion.infrastructure.web;

import com.cafepos.core.shared.seguridad.Usuario;

public record UsuarioRefResponse(Integer id, String nombre) {

    public static UsuarioRefResponse de(Usuario usuario) {
        return new UsuarioRefResponse(usuario.getId(), usuario.getNombre());
    }
}

package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.shared.seguridad.Usuario;

public record UsuarioAperturaResponse(Integer id, String nombre) {

    public static UsuarioAperturaResponse de(Usuario usuario) {
        return new UsuarioAperturaResponse(usuario.getId(), usuario.getNombre());
    }
}

package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.shared.seguridad.Usuario;

public record UsuarioCreadoResponse(Integer id, String nombre, String correo) {

    public static UsuarioCreadoResponse de(Usuario usuario) {
        return new UsuarioCreadoResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo());
    }
}

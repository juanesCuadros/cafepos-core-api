package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.UsuarioResumen;

import java.util.List;

public record UsuariosResponse(List<UsuarioListItemResponse> usuarios) {

    public static UsuariosResponse de(List<UsuarioResumen> resumenes) {
        return new UsuariosResponse(resumenes.stream().map(UsuarioListItemResponse::de).toList());
    }
}

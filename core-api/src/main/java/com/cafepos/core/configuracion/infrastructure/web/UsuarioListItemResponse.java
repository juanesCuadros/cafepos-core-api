package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.UsuarioResumen;

public record UsuarioListItemResponse(Integer id, String nombre, String correo, String rol, String empleadoAsociado,
                                       String estado) {

    public static UsuarioListItemResponse de(UsuarioResumen resumen) {
        return new UsuarioListItemResponse(resumen.id(), resumen.nombre(), resumen.correo(), resumen.rol(),
                resumen.empleadoAsociado(), resumen.estado());
    }
}

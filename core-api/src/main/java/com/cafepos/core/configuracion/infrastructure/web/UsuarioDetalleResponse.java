package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.UsuarioDetalle;

/** Whitelist explicito — nunca password_hash ni pin_autorizacion_hash (ver UsuarioDetalle). */
public record UsuarioDetalleResponse(Integer id, String nombre, String correo, Integer rolId, String rol,
                                      Integer empleadoId, String empleadoAsociado, String estado) {

    public static UsuarioDetalleResponse de(UsuarioDetalle detalle) {
        return new UsuarioDetalleResponse(detalle.id(), detalle.nombre(), detalle.correo(), detalle.rolId(),
                detalle.rol(), detalle.empleadoId(), detalle.empleadoAsociado(), detalle.estado());
    }
}

package com.cafepos.core.configuracion.domain;

/** Whitelist explicito para el detalle — nunca password_hash ni pin_autorizacion_hash. */
public record UsuarioDetalle(Integer id, String nombre, String correo, Integer rolId, String rol,
                              Integer empleadoId, String empleadoAsociado, String estado) {
}

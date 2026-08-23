package com.cafepos.core.configuracion.domain;

/** empleadoAsociado es null cuando el usuario no tiene empleado_id asignado. */
public record UsuarioResumen(Integer id, String nombre, String correo, String rol, String empleadoAsociado,
                              String estado) {
}

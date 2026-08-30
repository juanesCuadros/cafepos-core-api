package com.cafepos.core.personal.domain;

/** GET /empleados/{id} — null si ningun usuario tiene este empleado_id (ver indice unico idx_usuario_empleado_id_unico, V27). */
public record UsuarioAsociado(Integer id, String correo, String rol) {
}

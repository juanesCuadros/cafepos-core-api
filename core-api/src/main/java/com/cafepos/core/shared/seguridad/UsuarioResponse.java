package com.cafepos.core.shared.seguridad;

public record UsuarioResponse(Integer id, String nombre, String correo, String rol, Integer tenantId) {
}

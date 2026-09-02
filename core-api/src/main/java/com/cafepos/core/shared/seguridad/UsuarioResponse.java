package com.cafepos.core.shared.seguridad;

import java.util.List;

public record UsuarioResponse(Integer id, String nombre, String correo, String rol, Integer tenantId, List<String> permisos) {
}

package com.cafepos.admin.auth.infrastructure.web;

import com.cafepos.admin.auth.domain.Superadmin;

public record SuperadminPerfilResponse(
        Integer id,
        String nombre,
        String correo,
        String estado
) {
    public static SuperadminPerfilResponse de(Superadmin s) {
        return new SuperadminPerfilResponse(s.getId(), s.getNombre(), s.getCorreo(), s.getEstado());
    }
}

package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.shared.seguridad.Rol;

import java.util.List;

public record RolesResponse(List<RolResumenResponse> roles) {

    public static RolesResponse de(List<Rol> roles) {
        return new RolesResponse(roles.stream().map(RolResumenResponse::de).toList());
    }
}

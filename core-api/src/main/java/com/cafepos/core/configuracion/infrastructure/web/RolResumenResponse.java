package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.shared.seguridad.Rol;

public record RolResumenResponse(Integer id, String nombre, boolean esEditable) {

    public static RolResumenResponse de(Rol rol) {
        return new RolResumenResponse(rol.getId(), rol.getNombre(), rol.isEsEditable());
    }
}

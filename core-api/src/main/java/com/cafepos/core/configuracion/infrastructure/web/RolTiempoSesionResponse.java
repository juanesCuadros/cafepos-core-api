package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.RolTiempoSesion;

public record RolTiempoSesionResponse(Integer rolId, String rol, int minutosInactividad) {

    public static RolTiempoSesionResponse de(RolTiempoSesion tiempo) {
        return new RolTiempoSesionResponse(tiempo.rolId(), tiempo.rol(), tiempo.minutosInactividad());
    }
}

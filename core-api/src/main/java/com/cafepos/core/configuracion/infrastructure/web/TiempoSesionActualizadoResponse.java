package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.RolTiempoSesion;

public record TiempoSesionActualizadoResponse(Integer rolId, int minutosInactividad) {

    public static TiempoSesionActualizadoResponse de(RolTiempoSesion tiempo) {
        return new TiempoSesionActualizadoResponse(tiempo.rolId(), tiempo.minutosInactividad());
    }
}

package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.RolTiempoSesion;

import java.util.List;

public record TiemposSesionResponse(List<RolTiempoSesionResponse> tiempos) {

    public static TiemposSesionResponse de(List<RolTiempoSesion> tiempos) {
        return new TiemposSesionResponse(tiempos.stream().map(RolTiempoSesionResponse::de).toList());
    }
}

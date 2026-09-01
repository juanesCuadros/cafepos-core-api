package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.ModuloPermisos;

import java.util.List;

public record ModuloPermisosResponse(String modulo, List<PermisoMatrizItemResponse> permisos) {

    public static ModuloPermisosResponse de(ModuloPermisos moduloPermisos) {
        return new ModuloPermisosResponse(moduloPermisos.moduloPadre(),
                moduloPermisos.permisos().stream().map(PermisoMatrizItemResponse::de).toList());
    }
}

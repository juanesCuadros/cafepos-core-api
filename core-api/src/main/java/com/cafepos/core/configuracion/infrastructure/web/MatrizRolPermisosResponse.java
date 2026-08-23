package com.cafepos.core.configuracion.infrastructure.web;

import com.cafepos.core.configuracion.domain.MatrizRolPermisos;

import java.util.List;

public record MatrizRolPermisosResponse(String rol, boolean esEditable, List<ModuloPermisosResponse> modulos) {

    public static MatrizRolPermisosResponse de(MatrizRolPermisos matriz) {
        return new MatrizRolPermisosResponse(matriz.rol(), matriz.esEditable(),
                matriz.modulos().stream().map(ModuloPermisosResponse::de).toList());
    }
}

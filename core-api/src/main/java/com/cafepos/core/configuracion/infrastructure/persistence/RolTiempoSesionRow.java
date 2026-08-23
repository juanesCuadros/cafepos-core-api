package com.cafepos.core.configuracion.infrastructure.persistence;

interface RolTiempoSesionRow {

    Integer getRolId();

    String getRol();

    int getMinutosInactividad();
}

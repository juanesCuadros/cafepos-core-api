package com.cafepos.core.configuracion.infrastructure.persistence;

interface PermisoMatrizRow {

    Integer getPermisoId();

    String getModulo();

    String getAccion();

    boolean getActivo();
}

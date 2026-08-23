package com.cafepos.core.configuracion.infrastructure.persistence;

interface UsuarioRow {

    Integer getId();

    String getNombre();

    String getCorreo();

    Integer getRolId();

    String getRol();

    Integer getEmpleadoId();

    String getEmpleadoAsociado();

    String getEstado();
}

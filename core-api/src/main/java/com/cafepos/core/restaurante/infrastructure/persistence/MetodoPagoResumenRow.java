package com.cafepos.core.restaurante.infrastructure.persistence;

/** Proyeccion de la query nativa MetodoPagoJpaRepository.listar — alias exactos de la columna. */
interface MetodoPagoResumenRow {

    Integer getId();

    String getNombre();

    String getIcono();

    Boolean getEsEfectivo();

    String getEstado();

    String getCodigoFactus();
}

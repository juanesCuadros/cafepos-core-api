package com.cafepos.core.restaurante.infrastructure.persistence;

/** Proyeccion de la query nativa MesaJpaRepository.listarDeZona — alias exactos de la columna. */
interface MesaResumenRow {

    Integer getId();

    String getCodigo();

    String getNumero();

    Integer getCapacidad();

    String getEstado();
}

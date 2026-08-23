package com.cafepos.core.restaurante.infrastructure.persistence;

/** Proyeccion de la query nativa ZonaJpaRepository.listarConNumMesas — alias exactos de la columna. */
interface ZonaResumenRow {

    Integer getId();

    String getCodigo();

    String getIcono();

    String getNombre();

    Long getNumMesas();

    String getEstado();
}

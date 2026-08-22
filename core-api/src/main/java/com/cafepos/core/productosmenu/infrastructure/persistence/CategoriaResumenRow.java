package com.cafepos.core.productosmenu.infrastructure.persistence;

/** Proyeccion de la query nativa CategoriaJpaRepository.listarConNumProductos — alias exactos de la columna. */
interface CategoriaResumenRow {

    Integer getId();

    String getIcono();

    String getNombre();

    Integer getOrden();

    String getEstado();

    Long getNumProductos();
}

package com.cafepos.core.compras.infrastructure.persistence;

/** Proyeccion de la query nativa ProveedorJpaRepository.listar — alias exactos de la columna. */
interface ProveedorResumenRow {

    Integer getId();

    String getCodigo();

    String getNombre();

    String getNit();

    String getContacto();

    String getTelefono();

    String getEstado();
}

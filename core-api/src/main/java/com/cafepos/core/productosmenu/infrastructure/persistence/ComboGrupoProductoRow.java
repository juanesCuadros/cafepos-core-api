package com.cafepos.core.productosmenu.infrastructure.persistence;

/**
 * Proyeccion aplanada (grupo + producto) de ComboGrupoJpaRepository.gruposDe
 * — producto_id/producto_nombre vienen null cuando el grupo todavia no
 * tiene productos (LEFT JOIN), ver ComboRepositoryAdapter.gruposDe.
 */
interface ComboGrupoProductoRow {

    Integer getGrupoId();

    String getGrupoNombre();

    Integer getProductoId();

    String getProductoNombre();
}

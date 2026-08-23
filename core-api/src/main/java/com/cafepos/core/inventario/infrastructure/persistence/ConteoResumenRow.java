package com.cafepos.core.inventario.infrastructure.persistence;

import java.time.Instant;

/**
 * Proyeccion de la query nativa ConteoJpaRepository.listar — alias exactos
 * de la columna. fecha (TIMESTAMPTZ) se proyecta como Instant, no
 * OffsetDateTime — ver Javadoc de InsumoResumenRow para el motivo
 * (conversion la hace ConteoRepositoryAdapter).
 */
interface ConteoResumenRow {

    Integer getId();

    Instant getFecha();

    String getUsuarioNombre();

    Long getNumInsumos();

    Long getNumDiferencias();
}

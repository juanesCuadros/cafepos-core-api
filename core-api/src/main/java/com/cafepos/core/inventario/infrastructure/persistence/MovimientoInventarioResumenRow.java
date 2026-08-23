package com.cafepos.core.inventario.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Proyeccion de la query nativa MovimientoInventarioJpaRepository.listar —
 * alias exactos de la columna. fecha_hora (TIMESTAMPTZ) se proyecta como
 * Instant, no OffsetDateTime — ver Javadoc de InsumoResumenRow para el
 * motivo (conversion la hace MovimientoInventarioRepositoryAdapter).
 */
interface MovimientoInventarioResumenRow {

    Integer getId();

    Instant getFechaHora();

    String getInsumoNombre();

    String getTipo();

    BigDecimal getCantidad();

    String getUnidadMedida();

    String getUsuarioNombre();

    String getMotivoOrigen();

    String getReferenciaTipo();

    Integer getReferenciaId();
}

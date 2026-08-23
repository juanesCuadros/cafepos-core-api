package com.cafepos.core.clientes.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Proyeccion de la query nativa ClienteJpaRepository.historialDe — alias
 * exactos de la columna. fecha_hora (TIMESTAMPTZ) se proyecta como
 * Instant, NO OffsetDateTime (ver regla en CLAUDE.md, seccion
 * "Repositorios JPA" — una interfaz de proyeccion nativa no pasa por el
 * conversor de Hibernate que si aplica a una entidad JPA completa).
 */
interface CompraHistorialRow {

    Integer getVentaId();

    Instant getFechaHora();

    BigDecimal getTotal();

    String getFacturaNumero();

    String getEstado();
}

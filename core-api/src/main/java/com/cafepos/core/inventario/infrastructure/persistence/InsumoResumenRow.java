package com.cafepos.core.inventario.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Proyeccion de la query nativa InsumoJpaRepository.listar — alias exactos
 * de la columna. fecha_registro (TIMESTAMPTZ) se proyecta como Instant, NO
 * OffsetDateTime: a diferencia de una entidad JPA completa, una interfaz
 * de proyeccion nativa no pasa por el conversor de Hibernate para
 * java.time — Spring Data la devuelve tal cual la trae el driver JDBC
 * (Instant) y no sabe convertirla a OffsetDateTime sola (ver
 * InsumoRepositoryAdapter, que hace el .atOffset(ZoneOffset.UTC)).
 */
interface InsumoResumenRow {

    Integer getId();

    String getCodigo();

    String getNombre();

    Integer getCategoriaInsumoId();

    String getCategoriaInsumoNombre();

    String getUnidadMedida();

    BigDecimal getStockActual();

    BigDecimal getStockMinimo();

    BigDecimal getCostoActual();

    BigDecimal getValorTotal();

    String getEstadoStock();

    String getEstado();

    Instant getFechaRegistro();
}

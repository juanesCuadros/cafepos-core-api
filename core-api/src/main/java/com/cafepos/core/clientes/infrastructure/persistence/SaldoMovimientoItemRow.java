package com.cafepos.core.clientes.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Proyeccion de la query nativa ClienteJpaRepository.movimientosDe — alias
 * exactos de la columna. fecha (TIMESTAMPTZ) se proyecta como Instant, NO
 * OffsetDateTime (ver regla en CLAUDE.md, seccion "Repositorios JPA").
 */
interface SaldoMovimientoItemRow {

    Integer getId();

    String getTipo();

    BigDecimal getMonto();

    String getOrigenTipo();

    Integer getOrigenId();

    Instant getFecha();

    String getDescripcion();
}

package com.cafepos.core.caja.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/** Proyeccion nativa de DevolucionJpaRepository.listar — Instant, no OffsetDateTime (ver CLAUDE.md). */
interface DevolucionListadoRow {

    Integer getId();

    String getVentaCodigo();

    Instant getFecha();

    String getClienteNombre();

    BigDecimal getMontoDevuelto();

    String getMetodoReembolso();

    String getEstado();
}

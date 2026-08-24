package com.cafepos.core.caja.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/** Proyeccion nativa de FacturaDianJpaRepository.listar — Instant, no OffsetDateTime (ver CLAUDE.md). */
interface FacturaListadoRow {

    Integer getId();

    String getNumeroFactura();

    Instant getFechaEmision();

    String getClienteNombre();

    BigDecimal getTotal();

    String getEstadoDian();
}

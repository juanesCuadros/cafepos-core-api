package com.cafepos.core.contabilidad.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

interface CajaMovimientoContableRow {

    Instant getFechaHora();

    BigDecimal getMonto();

    String getMotivo();

    String getUsuarioNombre();
}

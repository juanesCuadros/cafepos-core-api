package com.cafepos.core.contabilidad.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

interface GastoContableRow {

    Instant getFechaHora();

    String getCodigo();

    BigDecimal getMonto();

    String getDescripcion();

    String getMetodoPago();

    String getUsuarioNombre();
}

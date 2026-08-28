package com.cafepos.core.contabilidad.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

interface CompraContableRow {

    Instant getFechaHora();

    String getCodigo();

    BigDecimal getTotal();

    String getProveedorNombre();

    String getUsuarioNombre();
}

package com.cafepos.core.contabilidad.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

interface VentaContableRow {

    Instant getFechaHora();

    String getCodigo();

    BigDecimal getTotal();

    String getMesaNumero();

    String getUsuarioNombre();

    String getMetodoPago();
}

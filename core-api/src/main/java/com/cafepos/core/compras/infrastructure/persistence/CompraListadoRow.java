package com.cafepos.core.compras.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Proyeccion de la query nativa CompraJpaRepository.listar — alias exactos de la columna. */
interface CompraListadoRow {

    Integer getId();

    String getCodigo();

    LocalDate getFecha();

    Integer getProveedorId();

    String getProveedorNombre();

    String getFormaPago();

    String getEstado();

    BigDecimal getTotal();
}

package com.cafepos.core.clientes.infrastructure.persistence;

import java.math.BigDecimal;

/** Proyeccion de la query nativa ClienteJpaRepository.buscarLiviano — alias exactos de la columna. */
interface ClienteBusquedaRow {

    Integer getId();

    String getNombre();

    String getTipoDocumento();

    String getNumeroDocumento();

    BigDecimal getSaldoFavor();
}

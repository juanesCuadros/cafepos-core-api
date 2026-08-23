package com.cafepos.core.clientes.infrastructure.persistence;

import java.math.BigDecimal;

/**
 * Proyeccion de la query nativa ClienteJpaRepository.listar — alias
 * exactos de la columna. numeroDocumento viaja SIN enmascarar aca a
 * proposito (es la fila interna de persistencia) — el enmascarado se
 * aplica en ClienteRepositoryAdapter (ver MascaraDocumento), nunca en SQL,
 * para tener la logica del caso raro (<4 digitos) en un solo lugar Java.
 */
interface ClienteResumenRow {

    Integer getId();

    String getCodigo();

    String getNombre();

    String getTipoDocumento();

    String getNumeroDocumento();

    String getTelefono();

    String getCorreo();

    BigDecimal getSaldoFavor();
}

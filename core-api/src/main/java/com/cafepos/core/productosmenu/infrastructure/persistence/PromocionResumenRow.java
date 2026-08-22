package com.cafepos.core.productosmenu.infrastructure.persistence;

import java.time.LocalDate;

/** Proyeccion de la query nativa PromocionJpaRepository.listar — alias exactos de la columna. */
interface PromocionResumenRow {

    Integer getId();

    String getNombre();

    String getTipoDescuento();

    LocalDate getVigenciaInicio();

    LocalDate getVigenciaFin();

    String getEstado();
}

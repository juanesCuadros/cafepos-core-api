package com.cafepos.core.productosmenu.infrastructure.persistence;

import java.math.BigDecimal;

/** Proyeccion de la query nativa ProductoJpaRepository.listarVisiblesParaMenuPublico — alias exactos de la columna. */
interface ProductoPublicoRow {

    String getCategoriaNombre();

    Integer getCategoriaOrden();

    String getNombre();

    String getDescripcion();

    BigDecimal getPrecioVenta();

    String getImagen();
}

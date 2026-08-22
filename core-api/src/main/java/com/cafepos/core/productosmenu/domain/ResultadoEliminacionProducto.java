package com.cafepos.core.productosmenu.domain;

/** Resultado de DELETE /productos/{id} — decide si hubo borrado fisico o soft-delete (ver ProductoService.eliminar). */
public enum ResultadoEliminacionProducto {
    ELIMINADO_FISICO,
    MARCADO_INACTIVO
}

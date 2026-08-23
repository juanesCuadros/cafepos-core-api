package com.cafepos.core.inventario.domain;

/** Resultado de DELETE /insumos/{id} — decide si hubo borrado fisico o soft-delete (ver InsumoService.eliminar). */
public enum ResultadoEliminacionInsumo {
    ELIMINADO_FISICO,
    MARCADO_INACTIVO
}

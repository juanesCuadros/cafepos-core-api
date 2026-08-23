package com.cafepos.core.operacion.application;

/** Una seleccion de POST /pedidos/{id}/items para un combo — un producto elegido por grupo. */
public record SeleccionCombo(Integer comboGrupoId, Integer productoId) {
}

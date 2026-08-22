package com.cafepos.core.productosmenu.domain;

import java.util.List;

/** Entrada de ComboService.crear: un grupo a crear junto con el combo, con sus productos ya resueltos por id. */
public record ComboGrupoCrear(String nombre, List<Integer> productosIds) {
}

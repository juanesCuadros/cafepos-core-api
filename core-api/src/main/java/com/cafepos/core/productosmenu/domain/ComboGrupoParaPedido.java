package com.cafepos.core.productosmenu.domain;

import java.util.List;

/** @NamedInterface propio, ver ComboParaPedido. */
@org.springframework.modulith.NamedInterface("comboGrupoParaPedido")
public record ComboGrupoParaPedido(Integer id, String nombre, List<Integer> productoIdsPermitidos) {
}

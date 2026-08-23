package com.cafepos.core.productosmenu.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos minimos que com.cafepos.core.operacion necesita para agregar un
 * combo a un pedido, incluyendo sus grupos con los producto_id validos para
 * cada uno (para validar las selecciones del request) — nunca las entidades
 * Combo/ComboGrupo completas.
 *
 * @NamedInterface propio, ver ComboService (tambien anotado).
 */
@org.springframework.modulith.NamedInterface("comboParaPedido")
public record ComboParaPedido(Integer id, String nombre, BigDecimal precio, String estado,
                               List<ComboGrupoParaPedido> grupos) {
}

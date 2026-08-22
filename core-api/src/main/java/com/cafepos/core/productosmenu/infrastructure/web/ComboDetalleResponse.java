package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.Combo;
import com.cafepos.core.productosmenu.domain.ComboGrupoDetalle;

import java.math.BigDecimal;
import java.util.List;

/**
 * Respuesta de POST/PATCH /combos, GET /combos/{id}, y de todos los
 * sub-endpoints de grupos — combo completo con grupos y productos
 * anidados (misma forma en todos, ver contrato api_04_productos_menu.md).
 */
public record ComboDetalleResponse(Integer id, String codigo, String nombre, BigDecimal precio,
                                    List<ComboGrupoResponse> grupos) {

    public static ComboDetalleResponse de(Combo combo, List<ComboGrupoDetalle> grupos) {
        return new ComboDetalleResponse(combo.getId(), combo.getCodigo(), combo.getNombre(), combo.getPrecio(),
                grupos.stream().map(ComboGrupoResponse::de).toList());
    }
}

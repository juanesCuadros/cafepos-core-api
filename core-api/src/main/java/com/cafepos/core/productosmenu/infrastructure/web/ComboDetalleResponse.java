package com.cafepos.core.productosmenu.infrastructure.web;

import com.cafepos.core.productosmenu.domain.Combo;
import com.cafepos.core.productosmenu.domain.ComboGrupoDetalle;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Respuesta de POST/PATCH /combos, GET /combos/{id}, y de todos los
 * sub-endpoints de grupos — combo completo con grupos y productos
 * anidados (misma forma en todos, ver contrato api_04_productos_menu.md).
 * descripcion/imagen/estado incluidos a proposito — el frontend
 * (combo.types.ts) ya los espera en esta respuesta, se habian quedado afuera.
 */
public record ComboDetalleResponse(Integer id, String codigo, String nombre, String descripcion, String imagen,
                                    @Monto BigDecimal precio, String estado, List<ComboGrupoResponse> grupos) {

    public static ComboDetalleResponse de(Combo combo, List<ComboGrupoDetalle> grupos) {
        return new ComboDetalleResponse(combo.getId(), combo.getCodigo(), combo.getNombre(), combo.getDescripcion(),
                combo.getImagen(), combo.getPrecio(), combo.getEstado(),
                grupos.stream().map(ComboGrupoResponse::de).toList());
    }
}

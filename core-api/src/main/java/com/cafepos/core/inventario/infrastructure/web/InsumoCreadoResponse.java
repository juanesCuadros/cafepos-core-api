package com.cafepos.core.inventario.infrastructure.web;

import com.cafepos.core.inventario.domain.Insumo;

import java.math.BigDecimal;

/** Respuesta de POST /insumos — misma forma minima que especifica el contrato. */
public record InsumoCreadoResponse(Integer id, String codigo, String nombre, BigDecimal stockActual,
                                    BigDecimal costoActual) {

    public static InsumoCreadoResponse de(Insumo insumo) {
        return new InsumoCreadoResponse(insumo.getId(), insumo.getCodigo(), insumo.getNombre(),
                insumo.getStockActual(), insumo.getCostoActual());
    }
}

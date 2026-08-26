package com.cafepos.core.gastos.infrastructure.web;

import com.cafepos.core.gastos.domain.CategoriaGasto;

public record CategoriaGastoItemResponse(Integer id, String nombre, String estado) {

    public static CategoriaGastoItemResponse de(CategoriaGasto categoriaGasto) {
        return new CategoriaGastoItemResponse(categoriaGasto.getId(), categoriaGasto.getNombre(),
                categoriaGasto.getEstado());
    }
}

package com.cafepos.core.compras.infrastructure.web;

import com.cafepos.core.compras.domain.Compra;

public record MarcarPagadaResponse(Integer id, String estado) {

    public static MarcarPagadaResponse de(Compra compra) {
        return new MarcarPagadaResponse(compra.getId(), compra.getEstado());
    }
}

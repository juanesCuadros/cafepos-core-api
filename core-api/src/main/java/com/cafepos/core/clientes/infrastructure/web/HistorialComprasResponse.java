package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.CompraHistorial;

import java.util.List;

/** Lista vacia hasta que exista el modulo Caja — correcto, no es un bug. */
public record HistorialComprasResponse(List<CompraHistorialResponse> compras) {

    public static HistorialComprasResponse de(List<CompraHistorial> compras) {
        return new HistorialComprasResponse(compras.stream().map(CompraHistorialResponse::de).toList());
    }
}

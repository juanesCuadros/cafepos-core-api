package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteRef;

public record ClienteVentaResponse(Integer id, String nombre) {

    public static ClienteVentaResponse de(ClienteRef cliente) {
        return cliente == null ? null : new ClienteVentaResponse(cliente.id(), cliente.nombre());
    }
}

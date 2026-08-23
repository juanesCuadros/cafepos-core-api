package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteBusqueda;

import java.util.List;

/** INFERENCIA PENDIENTE DE VERIFICAR contra api_03_caja.md — ver Javadoc de ClienteBusqueda. */
public record ClientesBusquedaResponse(List<ClienteBusquedaResponse> clientes) {

    public static ClientesBusquedaResponse de(List<ClienteBusqueda> resultados) {
        return new ClientesBusquedaResponse(resultados.stream().map(ClienteBusquedaResponse::de).toList());
    }
}

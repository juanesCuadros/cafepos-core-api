package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteResumen;

import java.util.List;

public record ClientesResponse(List<ClienteListItemResponse> clientes) {

    public static ClientesResponse de(List<ClienteResumen> resumenes) {
        return new ClientesResponse(resumenes.stream().map(ClienteListItemResponse::de).toList());
    }
}

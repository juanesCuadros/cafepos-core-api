package com.cafepos.core.caja.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteParaFactura;

public record ClienteFacturaResponse(Integer id, String nombre, String numeroDocumentoEnmascarado) {

    public static ClienteFacturaResponse de(ClienteParaFactura cliente) {
        return cliente == null ? null
                : new ClienteFacturaResponse(cliente.id(), cliente.nombre(), cliente.numeroDocumentoEnmascarado());
    }
}

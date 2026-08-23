package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteBusqueda;

import java.math.BigDecimal;

public record ClienteBusquedaResponse(Integer id, String nombre, String tipoDocumento,
                                       String numeroDocumentoEnmascarado, BigDecimal saldoFavor) {

    public static ClienteBusquedaResponse de(ClienteBusqueda busqueda) {
        return new ClienteBusquedaResponse(busqueda.id(), busqueda.nombre(), busqueda.tipoDocumento(),
                busqueda.numeroDocumentoEnmascarado(), busqueda.saldoFavor());
    }
}

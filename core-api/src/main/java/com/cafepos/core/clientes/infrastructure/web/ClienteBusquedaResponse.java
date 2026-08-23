package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteBusqueda;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record ClienteBusquedaResponse(Integer id, String nombre, String tipoDocumento,
                                       String numeroDocumentoEnmascarado, @Monto BigDecimal saldoFavor) {

    public static ClienteBusquedaResponse de(ClienteBusqueda busqueda) {
        return new ClienteBusquedaResponse(busqueda.id(), busqueda.nombre(), busqueda.tipoDocumento(),
                busqueda.numeroDocumentoEnmascarado(), busqueda.saldoFavor());
    }
}

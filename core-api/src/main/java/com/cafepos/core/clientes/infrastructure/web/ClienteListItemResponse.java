package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteResumen;
import com.cafepos.core.shared.jackson.Monto;

import java.math.BigDecimal;

public record ClienteListItemResponse(Integer id, String codigo, String nombre, String tipoDocumento,
                                       String numeroDocumentoEnmascarado, String telefono, String correo,
                                       @Monto BigDecimal saldoFavor) {

    public static ClienteListItemResponse de(ClienteResumen resumen) {
        return new ClienteListItemResponse(resumen.id(), resumen.codigo(), resumen.nombre(),
                resumen.tipoDocumento(), resumen.numeroDocumentoEnmascarado(), resumen.telefono(),
                resumen.correo(), resumen.saldoFavor());
    }
}

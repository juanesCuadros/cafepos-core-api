package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.ClienteBusqueda;

import java.util.List;

/**
 * GET /clientes/buscar — verificado contra api_03_caja.md 3.2 (ya en el
 * repo): el envoltorio es "resultados", no "clientes" (ajustado, antes
 * era una inferencia sin el contrato). Campos de ClienteBusquedaResponse
 * (id, tipo_documento, numero_documento_enmascarado, nombre, saldo_favor)
 * ya coincidian exacto.
 */
public record ClientesBusquedaResponse(List<ClienteBusquedaResponse> resultados) {

    public static ClientesBusquedaResponse de(List<ClienteBusqueda> resultados) {
        return new ClientesBusquedaResponse(resultados.stream().map(ClienteBusquedaResponse::de).toList());
    }
}

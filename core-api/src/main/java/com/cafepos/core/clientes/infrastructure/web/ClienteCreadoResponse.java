package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.Cliente;

/** Respuesta de POST/PATCH /clientes — misma forma minima que especifica el contrato para el POST. */
public record ClienteCreadoResponse(Integer id, String codigo, String nombre) {

    public static ClienteCreadoResponse de(Cliente cliente) {
        return new ClienteCreadoResponse(cliente.getId(), cliente.getCodigo(), cliente.getNombre());
    }
}

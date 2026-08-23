package com.cafepos.core.clientes.infrastructure.web;

import com.cafepos.core.clientes.domain.Cliente;

import java.math.BigDecimal;

/** GET /clientes/{id} — UNICO lugar donde numero_documento viaja completo, sin enmascarar. */
public record ClienteDetalleResponse(Integer id, String codigo, String tipoDocumento, String numeroDocumento,
                                      String nombre, String telefono, String correo, String direccion,
                                      BigDecimal saldoFavor) {

    public static ClienteDetalleResponse de(Cliente c) {
        return new ClienteDetalleResponse(c.getId(), c.getCodigo(), c.getTipoDocumento(), c.getNumeroDocumento(),
                c.getNombre(), c.getTelefono(), c.getCorreo(), c.getDireccion(), c.getSaldoFavor());
    }
}

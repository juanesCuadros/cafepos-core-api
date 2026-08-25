package com.cafepos.admin.negocios.domain;

/** tenant_id de la URL no corresponde a ningun negocio registrado. */
public class TenantNoEncontradoException extends RuntimeException {

    public TenantNoEncontradoException() {
        super("El negocio no existe");
    }
}

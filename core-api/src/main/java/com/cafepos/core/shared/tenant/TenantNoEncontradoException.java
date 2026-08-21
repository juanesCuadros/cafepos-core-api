package com.cafepos.core.shared.tenant;

/** El slug resuelto (header o subdominio) no corresponde a ningun negocio registrado. */
public class TenantNoEncontradoException extends RuntimeException {

    public TenantNoEncontradoException() {
        super("Negocio no encontrado");
    }
}

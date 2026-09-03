package com.cafepos.admin.negocios.domain;

public class OperacionTenantInvalidaException extends RuntimeException {
    public OperacionTenantInvalidaException(String mensaje) {
        super(mensaje);
    }
}

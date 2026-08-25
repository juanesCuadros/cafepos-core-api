package com.cafepos.core.compras.domain;

public class ProveedorNoEncontradoException extends RuntimeException {

    public ProveedorNoEncontradoException() {
        super("El proveedor no existe");
    }
}

package com.cafepos.core.compras.domain;

public class CompraNoEncontradaException extends RuntimeException {

    public CompraNoEncontradaException() {
        super("La compra no existe");
    }
}

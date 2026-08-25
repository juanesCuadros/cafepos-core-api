package com.cafepos.core.compras.infrastructure.web;

public record ProveedorEliminadoResponse(boolean eliminado) {

    public static final ProveedorEliminadoResponse ELIMINADO = new ProveedorEliminadoResponse(true);
}

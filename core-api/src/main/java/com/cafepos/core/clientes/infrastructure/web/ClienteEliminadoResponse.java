package com.cafepos.core.clientes.infrastructure.web;

public record ClienteEliminadoResponse(String mensaje) {

    public static final ClienteEliminadoResponse ELIMINADO = new ClienteEliminadoResponse("Cliente eliminado");
}

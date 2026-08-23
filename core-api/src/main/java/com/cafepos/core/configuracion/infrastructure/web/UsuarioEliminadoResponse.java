package com.cafepos.core.configuracion.infrastructure.web;

public record UsuarioEliminadoResponse(String mensaje) {

    public static final UsuarioEliminadoResponse ELIMINADO = new UsuarioEliminadoResponse("Usuario eliminado");
}

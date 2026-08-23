package com.cafepos.core.configuracion.infrastructure.web;

/** 400 — fallo o timeout de la conexion TCP real. */
public record ProbarConexionErrorResponse(boolean conectado, String error) {

    public static final ProbarConexionErrorResponse FALLIDA =
            new ProbarConexionErrorResponse(false, "No se pudo establecer conexión con la impresora");
}

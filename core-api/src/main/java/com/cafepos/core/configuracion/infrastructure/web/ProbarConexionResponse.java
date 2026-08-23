package com.cafepos.core.configuracion.infrastructure.web;

/** 200 — conexion TCP real exitosa. */
public record ProbarConexionResponse(boolean conectado, String mensaje) {

    public static final ProbarConexionResponse EXITOSA = new ProbarConexionResponse(true, "Conexion exitosa");
}

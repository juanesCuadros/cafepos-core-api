package com.cafepos.admin.auth.domain;

/** Ya existe un superadmin — el bootstrap es de un solo uso, para siempre. */
public class BootstrapNoDisponibleException extends RuntimeException {

    public BootstrapNoDisponibleException() {
        super("El registro inicial ya fue completado");
    }
}

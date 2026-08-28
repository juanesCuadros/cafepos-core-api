package com.cafepos.core.contabilidad.domain;

/** formato=pdf|excel diferido a proposito (ver DECISIONES YA TOMADAS) — 501, no un 400 ni un silencio. */
public class FormatoNoDisponibleException extends RuntimeException {

    public FormatoNoDisponibleException() {
        super("Exportacion a PDF/Excel no disponible todavia");
    }
}

package com.cafepos.core.restaurante.domain;

/** Defensivo — en teoria todo tenant tiene su fila de restaurantes provisionada al darse de alta. */
public class RestauranteNoConfiguradoException extends RuntimeException {

    public RestauranteNoConfiguradoException() {
        super("Información del restaurante no configurada");
    }
}

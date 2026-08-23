package com.cafepos.core.configuracion.domain;

/** Defensivo — en teoria todo tenant tiene su fila de configuracion_sistema provisionada al darse de alta. */
public class ConfiguracionSistemaNoConfiguradaException extends RuntimeException {

    public ConfiguracionSistemaNoConfiguradaException() {
        super("Configuración del sistema no encontrada");
    }
}

package com.cafepos.core.configuracion.domain;

import java.util.Optional;

/** Puerto de persistencia de ConfiguracionSistema — implementado en infrastructure.persistence. */
public interface ConfiguracionSistemaRepository {

    ConfiguracionSistema guardar(ConfiguracionSistema configuracionSistema);

    /** Registro unico por tenant — RLS ya garantiza que a lo sumo una fila es visible. */
    Optional<ConfiguracionSistema> buscarPorTenantActual();
}

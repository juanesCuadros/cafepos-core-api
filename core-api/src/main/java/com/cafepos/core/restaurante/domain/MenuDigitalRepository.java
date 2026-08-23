package com.cafepos.core.restaurante.domain;

import java.util.Optional;

/** Puerto de persistencia de MenuDigitalConfig — implementado en infrastructure.persistence. */
public interface MenuDigitalRepository {

    MenuDigitalConfig guardar(MenuDigitalConfig config);

    /** Puede no existir todavia — ver Javadoc de MenuDigitalConfig. */
    Optional<MenuDigitalConfig> buscarPorTenantActual();
}

package com.cafepos.core.restaurante.domain;

import java.util.Optional;

/** Puerto de persistencia de Restaurante — implementado en infrastructure.persistence. */
public interface RestauranteRepository {

    Restaurante guardar(Restaurante restaurante);

    /** Registro unico por tenant — RLS ya garantiza que a lo sumo una fila es visible. */
    Optional<Restaurante> buscarPorTenantActual();
}

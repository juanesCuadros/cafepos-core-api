package com.cafepos.admin.negocios.domain;

import java.util.Optional;

public interface RestauranteRepository {

    Optional<Restaurante> findByTenantId(Integer tenantId);

    Restaurante save(Restaurante restaurante);
}

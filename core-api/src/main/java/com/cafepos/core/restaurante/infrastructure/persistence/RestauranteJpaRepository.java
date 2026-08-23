package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.Restaurante;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface RestauranteJpaRepository extends TenantAwareRepository<Restaurante, Integer> {
}

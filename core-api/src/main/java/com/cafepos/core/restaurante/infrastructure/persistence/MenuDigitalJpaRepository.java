package com.cafepos.core.restaurante.infrastructure.persistence;

import com.cafepos.core.restaurante.domain.MenuDigitalConfig;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface MenuDigitalJpaRepository extends TenantAwareRepository<MenuDigitalConfig, Integer> {
}

package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.AreaCocina;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface AreaCocinaJpaRepository extends TenantAwareRepository<AreaCocina, Integer> {
}

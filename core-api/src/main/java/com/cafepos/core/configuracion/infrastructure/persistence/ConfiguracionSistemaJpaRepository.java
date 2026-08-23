package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.ConfiguracionSistema;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface ConfiguracionSistemaJpaRepository extends TenantAwareRepository<ConfiguracionSistema, Integer> {
}

package com.cafepos.core.configuracion.infrastructure.persistence;

import com.cafepos.core.configuracion.domain.Impresora;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface ImpresoraJpaRepository extends TenantAwareRepository<Impresora, Integer> {
}

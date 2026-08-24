package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.VentaPromocion;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface VentaPromocionJpaRepository extends TenantAwareRepository<VentaPromocion, Integer> {
}

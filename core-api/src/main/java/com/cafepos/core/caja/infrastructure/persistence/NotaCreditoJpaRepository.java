package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.NotaCredito;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface NotaCreditoJpaRepository extends TenantAwareRepository<NotaCredito, Integer> {

    boolean existsByFacturaId(Integer facturaId);
}

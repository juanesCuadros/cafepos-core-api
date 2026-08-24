package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.FacturaDian;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

interface FacturaDianJpaRepository extends TenantAwareRepository<FacturaDian, Integer> {

    Optional<FacturaDian> findByVentaId(Integer ventaId);
}

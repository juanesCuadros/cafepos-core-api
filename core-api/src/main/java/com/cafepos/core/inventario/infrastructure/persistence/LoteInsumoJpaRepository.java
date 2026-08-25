package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.LoteInsumo;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

interface LoteInsumoJpaRepository extends TenantAwareRepository<LoteInsumo, Integer> {

    Optional<LoteInsumo> findByCompraDetalleId(Integer compraDetalleId);
}

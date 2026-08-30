package com.cafepos.core.gastos.infrastructure.persistence;

import com.cafepos.core.gastos.domain.CategoriaGasto;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.List;

interface CategoriaGastoJpaRepository extends TenantAwareRepository<CategoriaGasto, Integer> {

    List<CategoriaGasto> findAllByOrderByNombre();
}

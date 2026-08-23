package com.cafepos.core.inventario.infrastructure.persistence;

import com.cafepos.core.inventario.domain.CategoriaInsumo;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.List;

interface CategoriaInsumoJpaRepository extends TenantAwareRepository<CategoriaInsumo, Integer> {

    List<CategoriaInsumo> findAllByOrderByNombre();
}

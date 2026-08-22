package com.cafepos.core.productosmenu.infrastructure.persistence;

import com.cafepos.core.productosmenu.domain.ComboGrupoProducto;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

interface ComboGrupoProductoJpaRepository extends TenantAwareRepository<ComboGrupoProducto, Integer> {

    Optional<ComboGrupoProducto> findByComboGrupoIdAndProductoId(Integer comboGrupoId, Integer productoId);
}

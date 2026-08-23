package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.PedidoItemComboSeleccion;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface PedidoItemComboSeleccionJpaRepository extends TenantAwareRepository<PedidoItemComboSeleccion, Integer> {
}

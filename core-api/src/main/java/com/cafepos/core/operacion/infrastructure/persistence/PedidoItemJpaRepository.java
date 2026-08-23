package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.PedidoItem;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.List;

interface PedidoItemJpaRepository extends TenantAwareRepository<PedidoItem, Integer> {

    List<PedidoItem> findByPedidoId(Integer pedidoId);
}

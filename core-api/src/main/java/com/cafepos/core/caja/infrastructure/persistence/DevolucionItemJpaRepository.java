package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.DevolucionItem;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.List;

interface DevolucionItemJpaRepository extends TenantAwareRepository<DevolucionItem, Integer> {

    List<DevolucionItem> findByDevolucionId(Integer devolucionId);
}

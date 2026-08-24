package com.cafepos.core.clientes.infrastructure.persistence;

import com.cafepos.core.clientes.domain.ClienteSaldoMovimiento;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

interface ClienteSaldoMovimientoJpaRepository extends TenantAwareRepository<ClienteSaldoMovimiento, Integer> {
}

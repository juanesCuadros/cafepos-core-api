package com.cafepos.core.caja.infrastructure.persistence;

import com.cafepos.core.caja.domain.CajaMovimiento;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.List;

interface CajaMovimientoJpaRepository extends TenantAwareRepository<CajaMovimiento, Integer> {

    List<CajaMovimiento> findByJornadaIdOrderByFechaHoraAsc(Integer jornadaId);
}

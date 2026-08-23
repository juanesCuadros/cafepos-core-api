package com.cafepos.core.operacion.infrastructure.persistence;

import com.cafepos.core.operacion.domain.Turno;
import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

interface TurnoJpaRepository extends TenantAwareRepository<Turno, Integer> {

    Optional<Turno> findByUsuarioIdAndHoraFinIsNull(Integer usuarioId);
}

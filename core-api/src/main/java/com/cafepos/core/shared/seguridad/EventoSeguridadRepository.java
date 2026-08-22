package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

public interface EventoSeguridadRepository extends TenantAwareRepository<EventoSeguridad, Long> {
}

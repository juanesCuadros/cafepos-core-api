package com.cafepos.core.shared.auditoria;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

public interface EventoAuditoriaRepository extends TenantAwareRepository<EventoAuditoria, Long> {
}

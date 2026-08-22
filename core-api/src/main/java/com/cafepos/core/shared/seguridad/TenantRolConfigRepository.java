package com.cafepos.core.shared.seguridad;

import com.cafepos.core.shared.tenant.TenantAwareRepository;

import java.util.Optional;

public interface TenantRolConfigRepository extends TenantAwareRepository<TenantRolConfig, Integer> {

    Optional<TenantRolConfig> findByTenantIdAndRolId(Integer tenantId, Integer rolId);
}

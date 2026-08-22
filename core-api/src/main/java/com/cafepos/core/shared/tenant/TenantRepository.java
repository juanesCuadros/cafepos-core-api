package com.cafepos.core.shared.tenant;

import java.util.Optional;

public interface TenantRepository extends TenantAwareRepository<Tenant, Integer> {

    Optional<Tenant> findBySlug(String slug);
}

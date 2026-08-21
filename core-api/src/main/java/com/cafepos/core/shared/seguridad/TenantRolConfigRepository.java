package com.cafepos.core.shared.seguridad;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRolConfigRepository extends JpaRepository<TenantRolConfig, Integer> {

    Optional<TenantRolConfig> findByTenantIdAndRolId(Integer tenantId, Integer rolId);
}

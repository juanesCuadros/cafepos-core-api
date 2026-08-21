package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.TenantRolConfig;
import com.cafepos.admin.negocios.domain.TenantRolConfigRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRolConfigJpaRepository
        extends JpaRepository<TenantRolConfig, Integer>, TenantRolConfigRepository {
}

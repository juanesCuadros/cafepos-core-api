package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.TenantPermisoConfig;
import com.cafepos.admin.negocios.domain.TenantPermisoConfigRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantPermisoConfigJpaRepository
        extends JpaRepository<TenantPermisoConfig, Integer>, TenantPermisoConfigRepository {
}

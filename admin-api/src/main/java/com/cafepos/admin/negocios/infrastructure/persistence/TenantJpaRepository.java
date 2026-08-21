package com.cafepos.admin.negocios.infrastructure.persistence;

import com.cafepos.admin.negocios.domain.Tenant;
import com.cafepos.admin.negocios.domain.TenantRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TenantJpaRepository extends JpaRepository<Tenant, Integer>, TenantRepository {

    @Override
    boolean existsBySlug(String slug);

    @Override
    List<Tenant> findByEstadoAndFechaProximaFacturacionBefore(String estado, LocalDate fecha);
}
